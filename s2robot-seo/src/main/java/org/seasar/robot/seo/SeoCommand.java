package org.seasar.robot.seo;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.robot.S2Robot;
import org.seasar.robot.seo.service.ReportService;

public class SeoCommand {

    private static String url;

    private static int depth;

    private static int count;
    
    private static int numOfThread;

    private static Options options;

    private static CommandLineParser parser;

    private static String outputFilename;

    private static String includeRegexps;

    private static String excludeRegexps;

    private static final String USAGE = "./run.sh [options] -u <url>";

    public static void main(String[] args) {
        SingletonS2ContainerFactory.init();
        S2Container container = SingletonS2ContainerFactory.getContainer();
        addOptions();
        parseOptions(args);

        System.out.println("URL: " + url);
        System.out.println("Depth: " + depth);
        System.out.println("Size: " + count);
        // S2Robotのコンポーネントを取得
        S2Robot s2Robot = (S2Robot) container.getComponent(S2Robot.class);
        // クロール対象のURLの取得
        s2Robot.addUrl(url);
        // クロール対象のURLを正規表現で設定
        for (String includeRegexp : includeRegexps.split(",")) {
            s2Robot.addIncludeFilter(includeRegexp);
        }
        if (excludeRegexps != null) {
            // クロールを除外するURLを正規表現で設定
            for (String excludeRegexp : excludeRegexps.split(",")) {
                s2Robot.addExcludeFilter(url + excludeRegexp);
            }
        }
        // クロールする深さの設定
        s2Robot.setMaxDepth(depth);
        // クロールするファイル数の設定
        s2Robot.setMaxAccessCount(count);
        // クロールするスレッド数の設定
        s2Robot.setNumOfThread(numOfThread);

        // クロールを開始
        System.out.println("Starting S2Robot.. ");
        String sessionId = s2Robot.execute();
        System.out.println("Finished: " + sessionId);

        // 結果の出力
        ReportService reportService = (ReportService) container
                .getComponent(ReportService.class);
        reportService.report(outputFilename, sessionId);

        // クロールデータの削除
        s2Robot.cleanup(sessionId);
    }

    private static void addOptions() {
        options = new Options();
        options.addOption("d", "depth", true, "depth");
        options.addOption("c", "count", true, "count");
        options.addOption("t", "thread", true, "num of thread");
        options.addOption("h", "help", false, "print help");
        options.addOption("o", "output", true,
                "name of output csv file (default: result.csv)");
        options.addOption("i", "include", true,
                "regexp for include filter (default: .*)");
        options.addOption("e", "exclude", true, "regexp for exclude filter");
        options.addOption("u", "url", true, "url to crawl");
    }

    private static void parseOptions(String[] args) {
        parser = new PosixParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        try {
            CommandLine line = parser.parse(options, args);
            // help
            if (line.hasOption("h") || line.getOptions().length == 0) {
                helpFormatter.printHelp(USAGE, options);
                System.exit(0);
            } else {
                // url
                if (line.hasOption("u")) {
                    url = line.getOptionValue("u");
                } else {
                    System.err.println("Please set a url to crawl it.");
                    helpFormatter.printHelp(USAGE, options);
                    System.exit(1);
                }
                // depth
                if (line.hasOption("d")) {
                    depth = Integer.parseInt(line.getOptionValue("d"));
                } else {
                    depth = -1;
                }
                // count
                if (line.hasOption("c")) {
                    count = Integer.parseInt(line.getOptionValue("c"));
                } else {
                    count = 0;
                }
                // numOfThread
                if (line.hasOption("t")) {
                    numOfThread = Integer.parseInt(line.getOptionValue("t"));
                } else {
                    numOfThread = 1;
                }
                // output file
                if (line.hasOption("o")) {
                    outputFilename = line.getOptionValue("o");
                } else {
                    outputFilename = "result.csv";
                }
                // include filter
                if (line.hasOption("i")) {
                    includeRegexps = line.getOptionValue("i");
                } else {
                    includeRegexps = url + ".*";
                }
                // exclude filter
                if (line.hasOption("e")) {
                    excludeRegexps = line.getOptionValue("e");
                }
            }
        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
        } catch (Exception e) {
            helpFormatter.printHelp(USAGE, options);
            e.printStackTrace();
            System.exit(1);
        }
    }

}

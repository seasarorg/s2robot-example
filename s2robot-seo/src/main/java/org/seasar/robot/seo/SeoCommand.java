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

    private static Options options;

    private static CommandLineParser parser;

    private static String outputFilename;

    private static String includeRegexp;

    private static String excludeRegexp;

    private static final String USAGE = "./run.sh [options] -u <url>";

    public static void main(String[] args) {
        SingletonS2ContainerFactory.init();
        S2Container container = SingletonS2ContainerFactory.getContainer();
        addOptions();
        parseOptions(args);

        System.out.println("URL: " + url);
        System.out.println("Depth: " + depth);
        S2Robot s2Robot = (S2Robot) container.getComponent(S2Robot.class);
        // add url
        s2Robot.addUrl(url);
        s2Robot.addIncludeFilter(url + includeRegexp);
        if (excludeRegexp != null) {
            s2Robot.addExcludeFilter(url + excludeRegexp);
        }
        // depth
        s2Robot.getRobotConfig().setMaxDepth(depth);

        // run s2robot
        System.out.println("Starting S2Robot.. ");
        String sessionId = s2Robot.execute();
        System.out.println("Finished: " + sessionId);

        // create report
        ReportService reportService = (ReportService) container
                .getComponent(ReportService.class);
        reportService.report(outputFilename, sessionId);

        // clean up
        s2Robot.cleanup(sessionId);
    }

    private static void addOptions() {
        options = new Options();
        options.addOption("d", "depth", true, "depth");
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
                    depth = 0;
                }
                // output file
                if (line.hasOption("o")) {
                    outputFilename = line.getOptionValue("o");
                } else {
                    outputFilename = "result.csv";
                }
                // include filter
                if (line.hasOption("i")) {
                    includeRegexp = line.getOptionValue("i");
                } else {
                    includeRegexp = ".*";
                }
                // exclude filter
                if (line.hasOption("e")) {
                    excludeRegexp = line.getOptionValue("e");
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

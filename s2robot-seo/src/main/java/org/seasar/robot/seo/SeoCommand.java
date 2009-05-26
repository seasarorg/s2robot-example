package org.seasar.robot.seo;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.robot.S2Robot;
import org.seasar.robot.seo.service.ReportService;

public class SeoCommand {

    public static void main(String[] args) {
        SingletonS2ContainerFactory.init();
        S2Container container = SingletonS2ContainerFactory.getContainer();

        if (args.length == 0) {
            System.err.println("No args. Please set a url to crawl it.");
            System.exit(1);
        }

        String url = args[0];
        int depth = 0;
        if (args.length > 1) {
            depth = Integer.parseInt(args[1]);
        }

        System.out.println("URL: " + url);
        System.out.println("Depth: " + depth);
        S2Robot s2Robot = (S2Robot) container.getComponent(S2Robot.class);
        // add url
        s2Robot.addUrl(url);
        s2Robot.addIncludeFilter(url + ".*");
        // depth
        s2Robot.getRobotConfig().setMaxDepth(depth);

        // run s2robot
        System.out.println("Starting S2Robot.. ");
        String sessionId = s2Robot.execute();
        System.out.println("Finished: " + sessionId);

        // create report
        ReportService reportService = (ReportService) container
                .getComponent(ReportService.class);
        reportService.report("result.csv", sessionId); //TODO filename

        // clean up
        s2Robot.cleanup(sessionId);
    }

}

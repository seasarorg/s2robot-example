package org.seasar.robot.seo;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.robot.S2Robot;
import org.seasar.robot.entity.AccessResult;
import org.seasar.robot.service.DataService;
import org.seasar.robot.util.AccessResultCallback;

public class SeoTool {
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
        // depth
        s2Robot.getRobotConfig().setMaxDepth(depth);

        // run s2robot
        System.out.println("Starting S2Robot.. ");
        String sessionId = s2Robot.execute();
        System.out.println("Finished: " + sessionId);

        // print urls
        DataService dataService = (DataService) container
                .getComponent(DataService.class);
        System.out.println("Crawled URLs: ");
        dataService.iterate(sessionId, new AccessResultCallback() {
            public void iterate(AccessResult accessResult) {
                System.out.println(accessResult.getUrl());
            }
        });

        // clean up
        s2Robot.cleanup(sessionId);
    }
}

package org.udg.pds.springtodo;

import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Point;
import org.udg.pds.springtodo.entity.Tag;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.repository.TagRepository;
import org.udg.pds.springtodo.repository.TaskRepository;
import org.udg.pds.springtodo.repository.UserRepository;
import org.udg.pds.springtodo.service.*;

import javax.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class Global {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private MinioClient minioClient;

    private final Logger logger = LoggerFactory.getLogger(Global.class);

    @Autowired
    private
    UserService userService;

    @Autowired
    private
    TaskService taskService;

    @Autowired
    private
    WorkoutService workoutService;

    @Autowired
    private
    RouteService routeService;

    @Autowired
    private
    PointService pointService;

    @Autowired
    private
    TagService tagService;

    @Autowired
    private Environment environment;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${todospring.minio.url:}")
    private String minioURL;

    @Value("${todospring.minio.access-key:}")
    private String minioAccessKey;

    @Value("${todospring.minio.secret-key:}")
    private String minioSecretKey;

    @Value("${todospring.minio.bucket:}")
    private String minioBucket;

    @Value("${todospring.base-url:#{null}}")
    private String BASE_URL;

    @Value("${todospring.base-port:8080}")
    private String BASE_PORT;

    @PostConstruct
    void init() {

        logger.info(String.format("Starting Minio connection to URL: %s", minioURL));
        try {
            minioClient = MinioClient.builder()
                                     .endpoint(minioURL)
                                     .credentials(minioAccessKey, minioSecretKey)
                                     .build();
        } catch (Exception e) {
            logger.warn("Cannot initialize minio service with url:" + minioURL + ", access-key:" + minioAccessKey + ", secret-key:" + minioSecretKey);
        }

        if (minioBucket.equals("")) {
            logger.warn("Cannot initialize minio bucket: " + minioBucket);
            minioClient = null;
        }

        if (BASE_URL == null) BASE_URL = "http://localhost";
        BASE_URL += ":" + BASE_PORT;

        initData();
    }

    private void initData() {

        if (activeProfile.equals("dev")) {
            logger.info("Starting populating database ...");

            User user = userService.register("usuari", "usuari@hotmail.com", "123456", 658658658, "John", "Doe", 22);
            IdObject taskId = taskService.addTask("Una tasca", user.getId(), new Date(), new Date());
            Tag tag = tagService.addTag("ATag", "Just a tag");
            taskService.addTagsToTask(user.getId(), taskId.getId(), new ArrayList<Long>() {{
                add(tag.getId());
            }});
            User user2 = userService.register("user", "user@hotmail.com", "0000", 123456789, "Tom", "Doe", 26);
            IdObject w1 = workoutService.addWorkoutWithRoute("running", user.getId(), 10.0,10.0);
            IdObject w2 = workoutService.addWorkoutWithRoute("hiking", user.getId(), 20.0,20.0);
            IdObject w3 = workoutService.addWorkoutWithRoute("cycling", user2.getId(), 30.0,30.0);
            IdObject w4 = workoutService.addWorkoutWithRoute("walking", user2.getId(), 40.0,40.0);
            ArrayList<Point> points = new ArrayList<>();
            ArrayList<Point> points2 = new ArrayList<>();
            ArrayList<Point> points3 = new ArrayList<>();
            ArrayList<Point> points4 = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                Double pointToAdd = 10.0 + i;
                points.add(new Point(pointToAdd,pointToAdd));
                points2.add(new Point(pointToAdd, pointToAdd));
                points3.add(new Point(pointToAdd, pointToAdd));
                points4.add(new Point(pointToAdd, pointToAdd));
            }
            pointService.addPoints(user.getId(), w1.getId(), points);
            pointService.addPoints(user.getId(), w2.getId(), points2);
            pointService.addPoints(user2.getId(), w3.getId(), points3);
            pointService.addPoints(user2.getId(), w4.getId(), points4);
        }
    }

    public MinioClient getMinioClient() {
        return minioClient;
    }

    public String getMinioBucket() {
        return minioBucket;
    }

    public String getBaseURL() {
        return BASE_URL;
    }
}

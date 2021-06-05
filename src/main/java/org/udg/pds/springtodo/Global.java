package org.udg.pds.springtodo;

import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.entity.*;
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
    ChatService chatService;

    @Autowired
    private
    EquipmentService equipmentService;

    @Autowired
    private
    ObjectiveService objectiveService;

    @Autowired
    private
    PostService postService;

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

            userService.registerFacebook("primer_facebook","primer_jzuhqwa_test@tfbnw.net","123",722722722,"primer","facebook",22,"EAADLlO4sHwMBAAzdIZBrhcHnSZCErqZAnVworWrZCmZAnXQ5MfCArgKLZCdN7FZAWTsm5uzZBZAZAdfIe1oooA7Y8U67LakKKbHMJvV68kdMoFUkzGnXC3ZCdvgaog7lFU4FDkMikJGTZCoWKLP7u2TG1vqOOJOhLQRv1lS4og7ObnLoQDKog2w5lzYGh5K0iE9FZAgdOFAV1xAWMFVmWG7DHFxiY3APWNtpD7dJeo1lgzuIdbgZDZD",Long.parseLong("104486745147691"));
            userService.registerFacebook("segon_facebook","segon_lughyxh_test@tfbnw.net","123",629792276,"segon","facebook",22,"EAADLlO4sHwMBAEqoqMAKfq8HyoUF75Low0ZC8cw2ykMQWLZBIYRIkf7EuXr4nUY1P0I2u5olhxss0KwMZCOJmyOapmS1BFQjEHKWnR8XSL6KjTbwLHwLkHaMcgMJ04fEE9SldfXT03itsZAzOemlWoHKyZCwyVVptThi0ZCOu2HlH1KOJrtLZByQZAHv2nmYX5iAT12guiLWes9KwHvznTgudqXmaKTXXSOZBog0rOhY7pLrSXbwxdZCS5",Long.parseLong("830415240907421"));

            User user = userService.register("usuari", "usuari@hotmail.com", "123456", 722180817, "John", "Doe", 22);
            IdObject taskId = taskService.addTask("Una tasca", user.getId(), new Date(), new Date());
            Tag tag = tagService.addTag("ATag", "Just a tag");
            taskService.addTagsToTask(user.getId(), taskId.getId(), new ArrayList<Long>() {{
                add(tag.getId());
            }});
            IdObject eq1 = equipmentService.addEquipment("Nike Platinum", "This shoes are so confortable and they are not expensive!", "http://localhost:8080/images/0cd2093b-b12e-47e3-a96a-965cd6ab4136.jpg", "https://www.nike.com/es/", user.getId());
            IdObject eq2 = equipmentService.addEquipment("Bike Leaf Green", "I really recommend to buy that bike. After 3 years it looks like it's still new!", "http://localhost:8080/images/0cd2093b-b12e-47e3-a96a-965cd6ab4136.jpg", "https://www.mountainbike.es/", user.getId());
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
                points.add(new Point(pointToAdd,pointToAdd, 2.0, 2.0, 2.0));
                points2.add(new Point(pointToAdd,pointToAdd, 2.0, 2.0, 2.0));
                points3.add(new Point(pointToAdd,pointToAdd, 2.0, 2.0, 2.0));
                points4.add(new Point(pointToAdd,pointToAdd, 2.0, 2.0, 2.0));
            }
            pointService.addPoints(user.getId(), w1.getId(), points);
            pointService.addPoints(user.getId(), w2.getId(), points2);
            pointService.addPoints(user2.getId(), w3.getId(), points3);
            pointService.addPoints(user2.getId(), w4.getId(), points4);

            postService.addPost(user.getId(), w1.getId(), "Morning run at the beach!", "");
            postService.addPost(user2.getId(), w3.getId(), "Testing my brand new bike!", "");

            IdObject o1 = objectiveService.addObjective("workouts", 5, user.getId());
            IdObject o2 = objectiveService.addObjective("distance", 5000.0, user.getId());
            IdObject o3 = objectiveService.addObjective("duration", 150.5, user.getId());

            chatService.createOneToOneChat(Long.valueOf(1), Long.valueOf(3));
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

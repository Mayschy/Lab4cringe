package org.example;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
class XmlSerializer {
    private final XStream xStream;

    public XmlSerializer() {
        xStream = new XStream(new StaxDriver());
        xStream.addPermission(AnyTypePermission.ANY);
        xStream.registerConverter(new ObservableListConverter(xStream.getMapper()));
    }

    public void serialize(Object obj, String filename) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filename);
             OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream)) {
            xStream.toXML(obj, writer);
        }
    }

    public Object deserialize(String filename) throws IOException, ClassNotFoundException {
        try (FileInputStream fileInputStream = new FileInputStream(filename);
             InputStreamReader reader = new InputStreamReader(fileInputStream)) {
            return xStream.fromXML(reader);
        }
    }
}

public class MainApplication extends Application {
    private final XmlSerializer serializer = new XmlSerializer();
    private CourseEntityObservable course;
    private TableView<LectureEntity> tableView;


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Головне вікно");

        // Створення меню
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("Файл");
        MenuItem newItem = new MenuItem("Створити новий набір даних");
        MenuItem loadItem = new MenuItem("Завантажити дані з XML");
        MenuItem saveItem = new MenuItem("Зберегти дані у XML");
        MenuItem exitItem = new MenuItem("Вихід");
        fileMenu.getItems().addAll(newItem, loadItem, saveItem, new SeparatorMenuItem(), exitItem);

        Menu editMenu = new Menu("Редагувати");
        MenuItem searchItem = new MenuItem("Пошук за ознаками");
        MenuItem sortItem = new MenuItem("Сортування за ознаками");
        editMenu.getItems().addAll(searchItem, sortItem);

        Menu helpMenu = new Menu("Довідка");
        MenuItem aboutItem = new MenuItem("Про програму");
        helpMenu.getItems().addAll(aboutItem);

        newItem.setOnAction(e -> {
            // Очистити попередні дані та створити новий набір даних
            createNewCourse();
        });

        aboutItem.setOnAction(e -> {
            showAboutDialog();
        });

        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
        loadItem.setOnAction(e -> {
            // Завантажити дані з XML
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Виберіть файл для завантаження");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("XML Files", "*.xml")
            );
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                course = loadCourseFromXML(selectedFile);
                if (course != null) {
                    // Оновлюємо дані у текстових полях
                    // Відображаємо дані у таблиці
                    tableView.setItems(course.getObservableList());
                    tableView.refresh();
                }
            }
        });


        saveItem.setOnAction(e -> {
            // Зберегти дані у XML
            if (course != null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Виберіть місце для збереження");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("XML Files", "*.xml")
                );
                File selectedFile = fileChooser.showSaveDialog(primaryStage);
                if (selectedFile != null) {
                    saveCourseToXML(course, selectedFile);
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Помилка", "Помилка збереження", "Немає даних для збереження.");
            }
        });

        exitItem.setOnAction(e -> primaryStage.close());


        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));




        tableView = new TableView<>();

        // Створення колонок
        TableColumn<LectureEntity, Date> dateColumn = new TableColumn<>("Дата");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<LectureEntity, String> courseNameColumn = new TableColumn<>("Назва курсу");
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("topic"));

        TableColumn<LectureEntity, Integer> studentCountColumn = new TableColumn<>("Кількість студентів");
        studentCountColumn.setCellValueFactory(new PropertyValueFactory<>("studentCount"));

        // Додавання колонок до таблиці
        tableView.getColumns().addAll(dateColumn, courseNameColumn, studentCountColumn);

        // Створення кореневого контейнера та налаштування розміщення елементів
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setLeft(leftPanel);
        root.setCenter(tableView);

        // Створення сцени та встановлення кореневого контейнера
        Scene scene = new Scene(root, 800, 600);

        // Встановлення сцени на вікно
        primaryStage.setScene(scene);
        primaryStage.show();


        TextField keywordField = new TextField();
        keywordField.setPromptText("Search keyword");
        // Створюємо кнопку для пошуку
        Button searchButton = new Button("Пошук");
        searchButton.setOnAction(e -> {
            // Перевірка наявності курсу
            if (course != null) {
                // Виклик методу сортування лекцій за темою
                LectureEntity[] searchResult = course.findLecturesByKeyword(keywordField.getText());
                if (searchResult == null)
                    System.out.println("Ничего не найдено");
                else
                    System.out.println("Результат пошуку: " + Arrays.toString(searchResult));
            } else {
                // Виведення повідомлення про помилку, якщо курс не було створено
                showAlert(Alert.AlertType.ERROR, "Помилка", "Помилка пошуку", "Не вдалося знайти дані. Спочатку створіть курс.");
            }
        });

        // Створюємо кнопку для сортування
        Button sortByWordCount = new Button("Сортування за кiлькiстю слiв");
        sortByWordCount.setOnAction(e -> {
            // Перевірка наявності курсу
            if (course != null) {
                // Виклик методу сортування лекцій за кількістю слів у темі
                List<LectureEntity> sortedLectures = course.sortLecturesByWordCount();
                // Виведення відсортованих лекцій (наприклад, у консоль)
                System.out.println("Відсортовані лекції: " + sortedLectures);
            } else {
                // Виведення повідомлення про помилку, якщо курс не було створено
                showAlert(Alert.AlertType.ERROR, "Помилка", "Помилка сортування", "Не вдалося відсортувати дані. Спочатку створіть курс.");
            }
        });

        Button sortByLecturesTopic = new Button("Сортування за студентами");
        sortByLecturesTopic.setOnAction(e -> {
            // Перевірка наявності курсу
            if (course != null) {
                // Виклик методу сортування лекцій за кількістю слів у темі
                List<LectureEntity> sortedLectures = course.insertionSortLecturesByTopic();
                // Виведення відсортованих лекцій (наприклад, у консоль)
                System.out.println("Відсортовані лекції: " + sortedLectures);
            } else {
                // Виведення повідомлення про помилку, якщо курс не було створено
                showAlert(Alert.AlertType.ERROR, "Помилка", "Помилка сортування", "Не вдалося відсортувати дані. Спочатку створіть курс.");
            }
        });

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem() != null) {
                LectureEntity selectedLecture = tableView.getSelectionModel().getSelectedItem();
                showEditLectureDialog(selectedLecture);
            }
        });
        Button addLectureButton = new Button("Додати лекцію");
        addLectureButton.setOnAction(e -> {
            showAddLectureDialog();
        });
        leftPanel.getChildren().addAll(keywordField, searchButton, sortByWordCount, sortByLecturesTopic, addLectureButton);



        createNewCourse();
    }
    private void showEditLectureDialog(LectureEntity lecture) {
        Stage editStage = new Stage();
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.setTitle("Редагування лекції");

        VBox editLayout = new VBox(10);
        editLayout.setPadding(new Insets(10));

        // Додайте поля введення для редагування даних лекції
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(lecture.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        TextField topicField = new TextField(lecture.getTopic());
        TextField studentCountField = new TextField(String.valueOf(lecture.getStudentCount()));

        Button saveButton = new Button("Зберегти");
        saveButton.setOnAction(event -> {
            lecture.setDate(Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            lecture.setTopic(topicField.getText());
            lecture.setStudentCount(Integer.parseInt(studentCountField.getText()));

            tableView.refresh();
            editStage.close();
        });

        Button deleteButton = new Button("Видалити");
        deleteButton.setOnAction(event -> {
            deleteLecture(lecture);
            editStage.close();
        });

        editLayout.getChildren().addAll(new Label("Дата:"), datePicker, new Label("Назва курсу:"), topicField, new Label("Кількість студентів:"), studentCountField, saveButton, deleteButton);


        Scene editScene = new Scene(editLayout, 300, 300);
        editStage.setScene(editScene);
        editStage.show();
    }
    private void createNewCourse() {
        // Створення нового набору даних та повернення його
        List<LectureEntity> lectures = new ArrayList<>();
        // Додавання лекцій до списку

        // Додавання декількох прикладів лекцій для початкових даних
        lectures.add(new LectureEntity(parseDate("2023-10-15"), "Basic concepts of Java programming", 30));
        lectures.add(new LectureEntity(parseDate("2023-10-15"), "Understanding OOP principles", 50));
        lectures.add(new LectureEntity(parseDate("2023-10-15"), "Working with collections in Java", 60));

        // Встановлення значення course на новостворений курс
        course = new CourseEntityObservable("Java Course", "Fufelsmersth", lectures);

        // Додавання лекцій до таблиці
        tableView.setItems(course.getObservableList());
        tableView.refresh();


    }
    private void showAddLectureDialog() {
        Stage addStage = new Stage();
        addStage.initModality(Modality.APPLICATION_MODAL);
        addStage.setTitle("Створення нової лекції");

        VBox addLayout = new VBox(10);
        addLayout.setPadding(new Insets(10));

        DatePicker datePicker = new DatePicker();
        TextField topicField = new TextField();
        TextField studentCountField = new TextField();

        Button addButton = new Button("Додати");
        addButton.setOnAction(event -> {
            String topic = topicField.getText();
            Date date = Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            int studentCount = Integer.parseInt(studentCountField.getText());

            LectureEntity newLecture = new LectureEntity(date, topic, studentCount);

            // Додаємо нову лекцію до списку лекцій у course

            // Оновлюємо дані у tableView
            tableView.getItems().add(newLecture);

            addStage.close();
        });

        addLayout.getChildren().addAll(new Label("Дата:"), datePicker, new Label("Назва курсу:"), topicField, new Label("Кількість студентів:"), studentCountField, addButton);

        Scene addScene = new Scene(addLayout, 300, 300);
        addStage.setScene(addScene);
        addStage.show();
    }




    private CourseEntityObservable loadCourseFromXML(File file) {
        try {
            Object obj = serializer.deserialize(file.getAbsolutePath());
            if (obj instanceof CourseEntityObservable) {
                return (CourseEntityObservable) obj;
            } else {
                // Обробка помилки, якщо зчитаний об'єкт не є типом CourseEntityObservable
                throw new IOException("Неправильний тип об'єкта");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Помилка", "Помилка завантаження", "Не вдалося завантажити дані з файлу.");
            return null;
        }
    }


    // В методі saveCourseToXML(CourseEntityWithStream course, File file):
    private void saveCourseToXML(CourseEntityObservable course, File file) {
        try {
            serializer.serialize(course, file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Помилка", "Помилка збереження", "Не вдалося зберегти дані у файл.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void deleteLecture(LectureEntity lecture) {
        if (lecture != null) {
            course.removeLecture(lecture);
            tableView.getItems().remove(lecture);
        }
    }
    public static void main(String[] args) {
        launch(args);
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Про програму");
        alert.setHeaderText("Про програму");
        alert.setContentText("Васильєв М.О");
        alert.showAndWait();
    }

    public static Date parseDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            return null;
        }
    }
}
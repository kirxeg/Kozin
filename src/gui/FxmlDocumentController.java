package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import managementprocessthread.MemoryManage;
import managementprocessthread.Process;
import managementprocessthread.Thread;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import main.Main;

/**
 * FXML Controller class
 *
 * @author Admin
 */
public class FxmlDocumentController implements Initializable {

    @FXML
    private ListView<Process> listViewProcess;
    @FXML
    private ListView<Thread> listViewThread;
    @FXML
    private TextField textFieldMasLenght;
    @FXML
    private TextField textFieldIdPrevProcess;
    @FXML
    private Button buttonAddProcess;
    @FXML
    private Button buttonRemoveProcess;
    @FXML
    private TextField textFieldWorkTimeThread;
    @FXML
    private Button buttonAddThread;
    @FXML
    private Button buttonRemoveThread;
    
    @FXML
    private Label labelSumTimeThread;
    @FXML
    private Label labelSumAllTimeThread;
    
    @FXML
    private Button buttonLoad;
    @FXML
    private Button buttonSave;
    
    private ObservableList<Process> listProcess;
    private ObservableList<Thread> listThread;
    private MemoryManage memoryManage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        memoryManage = new MemoryManage();
        //Инициализация наблюдающих листов
        listProcess = FXCollections.observableArrayList();
        listThread = FXCollections.observableArrayList();
        //Установка значений для компонентов ListView
        listViewProcess.setItems(listProcess);
        listViewThread.setItems(listThread);
        //Биндинг для кнопки buttonAddThread - пока поля пустые и не выбрано значение в listViewProcess, кнопка не активна
        buttonAddThread.disableProperty().bind(textFieldWorkTimeThread.textProperty().isEmpty()
            .or(listViewProcess.getSelectionModel().selectedItemProperty().isNull()));
        //Биндинг кнопки buttonRemoveProcess - пока не выбрано значение в listViewProcess, кнопка не активна
        buttonRemoveProcess.disableProperty().bind(listViewProcess.getSelectionModel().selectedItemProperty().isNull());
        //Биндинг кнопки buttonRemoveThread - пока нет значений в listViewThread, кнопка не активна
        buttonRemoveThread.disableProperty().bind(Bindings.size(listViewThread.itemsProperty().get()).isEqualTo(0));
        
        labelSumTimeThread.setText("-");
        labelSumAllTimeThread.setText("-");
        addListChangeListener();
        
        setDisableButtonAddProcess();
    }
    /**
     * Метод установки слушателя выбора строки в листе
     */
    private void addListChangeListener(){
        listViewProcess.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Process>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Process> c) {
                //Если выбраны строки, то
                if(!c.getList().isEmpty()){
                    //Получение объекта типа Process
                    Process process = c.getList().get(0);
                    //Если значение process!=null, то
                    if(process!=null){
                        //Обновление листа с потоками
                        refreshListThread(process);
                        //Установка текста в компонент labelSumTimeThread
                        labelSumTimeThread.setText(String.valueOf(process.allWorkTime()));
                    } else {
                        //Очистка листа с потоками
                        listThread.clear();
                        labelSumTimeThread.setText("-");
                    }
                    labelSumAllTimeThread.setText(String.valueOf(memoryManage.sumAllTime()));
                } else {
                    listThread.clear();
                    labelSumTimeThread.setText("-");
                }
            }
        });
    }

    /**
     * Метод обновление листа с процессами
     */
    private void refreshListProcess(){
        listProcess.clear();
        Process current = memoryManage.getFirstProcess();
        while(current!=null){
            listProcess.add(current);
            current = current.getNextProcess();
        }
    }
    
    /**
     * Метод обновления листа с потоками
     * @param selected выделенный процесс
     */
    private void refreshListThread(Process selected){
        listThread.clear();
        Thread[] arrayThread = selected.getArrayThread();
        for(int i=0; i< selected.getThreadCount(); i++){
            listThread.add(arrayThread[i]);
        }
    } 
    /**
     * Метод, вызываемый при нажатии на кнопку Добавить процесс
     */
    @FXML
    private void buttonAddProcessOnAction(){
        //Получение значений из текстовых полей
        int masLenght = Integer.parseInt(textFieldMasLenght.getText());
        int idPrevProcess;
        if(memoryManage.getFirstProcess()!=null){
            idPrevProcess = Integer.parseInt(textFieldIdPrevProcess.getText());
        } else {
            idPrevProcess = 1;
            textFieldIdPrevProcess.setText("1");
        }
        //Добавление процесса
        Process process = memoryManage.addAfterProcess(masLenght, idPrevProcess);
        //Если process!=null, то
        if(process!=null){
            //Обновление листа с процессами
            refreshListProcess();
            labelSumAllTimeThread.setText(String.valueOf(memoryManage.sumAllTime()));
            setDisableButtonAddProcess();
        } else {
            Process.decrementNextIdThread();
            //Если процесс не был добавлен, то вызывается отображение модального диалогового окна с сообщением
            Alarm.showAlert(Alert.AlertType.INFORMATION, "Ошибка добавления просесса", 
                    "Процесс не был добавлен", "Процесс с id " + idPrevProcess + ", \nпосле которого нужно добавить новый процесс не найден");
            
        }
    }
    
    /**
     * Метод, вызываемый при нажатии на кнопку Добавления поотока
     */
    @FXML
    private void buttonAddThreadOnAction(){
        //Получение индекса выделенного процесса из листа процессов
        int selectedIndex = listViewProcess.getSelectionModel().getSelectedIndex();
        //Получение значений из текстовый полей
        float timeWork = Float.parseFloat(textFieldWorkTimeThread.getText());
        //Получение объекта типа Process из листа процессов
        Process process = listViewProcess.getSelectionModel().getSelectedItem();
        //Если process!=null, то
        if(process!=null){
            //Добавление потока в выделенный процесс
            process.addThread(timeWork);                             
            //Обновление листа с процессами
            refreshListProcess();
            //Выбор процесса в листе процессов
            listViewProcess.getSelectionModel().select(selectedIndex);
            //Обновление листа с потоками
            refreshListThread(process);
            //Установка значения суммарного времени в текстовую метку labelSumAllTimeThread
            labelSumAllTimeThread.setText(String.valueOf(memoryManage.sumAllTime()));
            
        }
    }
    
    /**
     * Метод, вызываемый при нажатии на кнопку Удаления процесса
     */
    @FXML
    private void buttonRemoveProcessOnAction(){
        //Получение процесса из листа с процессами
        Process process = listViewProcess.getSelectionModel().getSelectedItem();
        //Удаление процесса
        if(process.getId() == 1){
            Alarm.showAlert(Alert.AlertType.INFOMATION, "Ошибка удаления процесса",
                    "Процесс не был удален", "\nтак как это первый процесс");
        } else {
            memoryManage.removeProcess(process.getId());
        }
        //Обновление листа с процессами
        refreshListProcess();
        labelSumAllTimeThread.setText(String.valueOf(memoryManage.sumAllTime()));
        setDisableButtonAddProcess();
    }
    
    /**
     * Метод, вызываемый при нажатии на кнопку Удаления потока.
     * Удаление потока происходит по принципу очереди - FIFO
     */
    @FXML
    private void buttonRemoveThreadOnAction(){
        //Получение процесса из листа с процессами
        Process process = listViewProcess.getSelectionModel().getSelectedItem();
        //Получение потока из листа с потоками
        Thread thread = listViewThread.getSelectionModel().getSelectedItem();
        //Удаление потока
        boolean remove = process.removeThread();
        if(remove){
            //Обновление листа с потоками
            refreshListThread(process);
            //Установка значений в текстовые метки
            labelSumTimeThread.setText(String.valueOf(process.allWorkTime()));
            labelSumAllTimeThread.setText(String.valueOf(memoryManage.sumAllTime()));
        }
    }
    
    /**
    * Слушатеть событий типа нажатой клавиши. Если нажата не цифра, то событие прерывается
    * Данный метод используется на всех текстовых поляй для ограничения ввода.
    * В текстовое поле может записаться только цифра, введенная с клавиатуры
    * @param event 
    */
    @FXML
    private void setOnKeyTyped(KeyEvent event) {
        //Получение массива значений введенного символа
        char[] ch = event.getCharacter().toCharArray();
        //Если введена не цифра, то
        if (!Character.isDigit(ch[0])){
            //Прерывание события
            event.consume();           
        }
    }

    /**
     * Метод, вызываемый при нажатии на кнопку Сохранить 
     */
    @FXML
    private void buttonSaveOnAction(){
        //Создание объекты типа FileChooser
        FileChooser fc = new FileChooser();
        //Установка названия объекту
        fc.setTitle("Окно сохранения файла");
        //Отображение окна для уазания путь сохранения файла
        File file = fc.showSaveDialog(Main.getStage());
        if(file!=null){
            //Получение абсолютного пути
            String path = file.getAbsolutePath();
            boolean save = false;
            try {
                //Сохранение файла
                save = memoryManage.saveFile(path);
            } catch (IOException ex) {
                Logger.getLogger(FxmlDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                Alarm.showAlert(Alert.AlertType.ERROR, "Сохранение", "Ошибка сохранения", "Объект не был сохранен.");
            }        
            if(!save){
                Alarm.showAlert(Alert.AlertType.ERROR, "Сохранение", "Ошибка сохранения", "Объект не создан.");
            }
        }
    }
    
    @FXML
    private void buttonLoadOnAction(){
        //Диалоговое окно
        FileChooser fc = new FileChooser();
        fc.setTitle("Окно выбора файла");
        //Путь к файлу
        File file = fc.showOpenDialog(Main.getStage());
        if(file!=null){
            String path = file.getAbsolutePath();
            try {
                Process process = memoryManage.loadFile(path);
                memoryManage.setFirstProcess(process);
                refreshListProcess();
                labelSumAllTimeThread.setText(String.valueOf(memoryManage.sumAllTime()));
                setDisableButtonAddProcess();
            } catch (IOException ex) {
                Logger.getLogger(FxmlDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                Alarm.showAlert(Alert.AlertType.ERROR, "Загрузка", "Ошибка загрузки", "Объект не был загружен.");
            }
        }
    }
    /**
     * Метод активации декативации кнопки 
     */
    private void setDisableButtonAddProcess() {
        //Если ссылка на первый элемента объекта типа Process равен null, то
        if(memoryManage.getFirstProcess() == null){
            //Биндинг для кнопки buttonAddProcess - пока поле textFieldMasLenght не заполнено, кнопка не активна
            buttonAddProcess.disableProperty().bind(textFieldMasLenght.textProperty().isEmpty());
            //Деактивация текстовой панели textFieldIdPrevProcess
            textFieldIdPrevProcess.setDisable(true);
        } else {
            //Биндинг для кнопки buttonAddProcess - пока поле textFieldMasLenght 
            //и textFieldIdPrevProcess не заполнено, кнопка не активна
            buttonAddProcess.disableProperty().bind(textFieldMasLenght.textProperty().isEmpty()
                .or(textFieldIdPrevProcess.textProperty().isEmpty()));
            //Активация текстовой панели textFieldIdPrevProcess
            textFieldIdPrevProcess.setDisable(false);
        }        
    }

}

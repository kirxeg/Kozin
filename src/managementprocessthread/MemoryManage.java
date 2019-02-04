package managementprocessthread;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class MemoryManage {
    
    /**
     * Ссылка на первый элемента объекта типа Process
     */
    private Process firstProcess;


    public MemoryManage() {
        
        firstProcess = null;
        
    }

    public Process getFirstProcess() {
        return firstProcess;
    }

    public void setFirstProcess(Process firstProcess) {
        this.firstProcess = firstProcess;
    }

    /**
     * Добавление процесса в список процессов после заданного
     * @param arraySize размер массива
     * @param afterID идентификатор процесса, после которого необходимо добавить
     * новый процесс
     * @return объект типа Process, null - если не найден afterID или процесс с таким id уже существует
     */
    public Process addAfterProcess(int arraySize, int afterID){
        
        Process newProcess = null;
        //Получение значения нового автоинкрементного поля autoincrement
        int id = Process.getNextIdProcess();
        
        if(firstProcess == null){
            firstProcess = new Process(id, null, arraySize);
            //firstProcess.setNextProcess(headProcess);
            newProcess = firstProcess;
        } else {
            //Поиск процесса по заданному id
            Process findIdProcess = searchProcess(id);
            //Если найденный процесс равен null, то
            if(findIdProcess==null){  
                //Поиск процесса, после которого необходимо добавить новый процесс
                Process findProcess = searchProcess(afterID);
                //Если найденный процесс не равен null, то
                    if(findProcess!=null){
                        //Получение ссылки на процесс, идущий за найденным процессом
                        Process nextProcess = findProcess.getNextProcess();
                        //Инициализация нового процесса
                        newProcess = new Process(id, nextProcess, arraySize);
                        //Установка следующего процесса значением нового процесса у процесса,
                        //после которого добавляется новый процесс
                        findProcess.setNextProcess(newProcess);
                    }
            }
        }
        return newProcess;
    }

    /**
     * Метод удаления процесса
     * @param id идентификатор процесса
     * @return true - если процесс удален, иначе false
     */
    public boolean removeProcess(int id){
        boolean remove = false;
        //Маркер для случая удаления первого процесса
        boolean first = true;

        Process current = firstProcess;
        Process previous = firstProcess;
        while(current!=null){
            if(current.getId()==id){
                Process next = current.getNextProcess();
                if(first){
                    remove = false;
                    break;
                } else {                   
                    previous.setNextProcess(next);
                    remove = true;
                }
                break;
            } else {
                first = false;
                previous = current;
                current = current.getNextProcess();               
            }
        }
        return remove;
    }

    /**
     * Метод нахождения процесса
     * @param id идентификатор процесса
     * @return объект типа Process, null - если процесс не найден
     */
    public Process searchProcess(int id){
        //Текущий процесс
        Process current = firstProcess;
        //Найденный процесс
        Process find = null;
        
        while(current!=null){
            if(current.getId()==id){
                find = current;
                break;
            } else {
                current = current.getNextProcess();
            }
        }
        return find;
    }

    /**
     * Метод нахождения количества всех процессов
     * @return сумма всех процессов
     */
    public int sumAllProcess(){
        Process current = firstProcess;
        int count = 0;
        while(current!=null){
            count++;
            current = current.getNextProcess();
        }
        return count;
    }
    
    /**
     * Метод нахождения общего времени работы всех потоков в процессах
     * @return общее время работы
     */
    public float sumAllTime(){
        Process current = firstProcess;
        float time = 0;
        while (current != null) {
            time += current.allWorkTime();
            current = current.getNextProcess();
        }
        return time;
    }

    /**
     * Метод сохранения объекта в файл
     * @param path путь к файлу
     * @return true - если объек успешно сохранен, иначе - false
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public boolean saveFile(String path) throws FileNotFoundException, IOException {
        boolean save = false;
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))){
            if(firstProcess!=null){
                oos.writeObject(firstProcess);
                save = true;
            }
        }
        return save;       
    }
    
    /**
     * Метод загрузки объекта из файла
     * @param path путь к файлу
     * @return объект типа Process
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public Process loadFile(String path) throws FileNotFoundException, IOException {
        Process process = null;
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))){
            try {
                process = (Process) ois.readObject();
                //Получение значение последней автоинкрементной id
                int id = 0;
                Process current = process;
                while(current!=null){
                    int idCur = current.getId();
                    //Если id из текущего процесса idCur больше чем id, то 
                    if(idCur>id){
                        id = idCur;
                    }
                    //Получить ссылку на следующий процесс
                    current = current.getNextProcess();
                }
                //Установить актуальное значение статического поля nextIdProcess
                Process.setActualIdProcess(id);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MemoryManage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return process;
    }
}

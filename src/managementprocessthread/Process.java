package managementprocessthread;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author Admin
 */
public class Process implements Serializable{
    private static final long serialVersionUID = 1L;
    private static int nextIdProcess = 0;
    private int nextIdThread = 0;
    private int id;//Айди процесса
    private Thread[] arrayOfThread;//Набор очередей
    private Process nextProcess;//Указатель на следующий процесс
    private int threadCount;//Количество элементов в очереди
    private int arraySize;//Размер массива

    public Process(int id, Process nextProcess, int arraySize) {
        
        this.id = id;
        this.nextProcess = nextProcess;
        this.arraySize = arraySize;
        arrayOfThread = new Thread[this.arraySize];
        
    }

    public int getArraySize() {
        return arraySize;
    }//Размер массива

    public Thread[] getArrayThread(){
        return arrayOfThread;
    }

    /**
     * Метод получения количества созданных потоков
     * @return 
     */
    public int getThreadCount() {
        return threadCount;
    }

    public Process getNextProcess() {
        return nextProcess;
    }//Гет

    public void setNextProcess(Process nextProcess) {
        this.nextProcess = nextProcess;
    }//Сет

    /**
     * Общее время работы всех потоков данного процесса
     * @return время работы
     */
    public float allWorkTime(){
        float time = 0;
        for (int i = 0; i < threadCount; i++){
            time += arrayOfThread[i].getWorkTime();
        }
        return time;
    }

    /**
     * Метод добавления очередного потока в процесс
     * @param workTime время работы потока
     */
    public void addThread(float workTime){
        int id = getNextIdThread();
        if (threadCount == arraySize){
            resize(arraySize * 2);
        }
        arrayOfThread[threadCount] = new Thread(id,workTime);
        threadCount++;
    }

    /**
     * Метод увелически длины массива
     * @param newLength 
     */
    private void resize(int newLength){
        Thread[] newArrayThread = new Thread[newLength];
        System.arraycopy(arrayOfThread,0, newArrayThread,0,threadCount);
        arrayOfThread = newArrayThread;
        arraySize = newLength;
    }

    /**
     * Метод удаления потока из процесса
     * @return true - если поток найден и удален, иначе - false
     */
    public boolean removeThread(){
        //Если количество потоков равно 0, то сразу вернуть false
        if(threadCount == 0){
            return false;
        }
        //Удаление потока
        arrayOfThread[0] = null;
        //Сдвиг всех элементов массива с потоками на 1 влево
        System.arraycopy(arrayOfThread, 1, arrayOfThread, 0, threadCount-1);
        //Уменьшение счетчика количества потоков на 1
        threadCount--;
        return true;
    }

    /**
     * Метод получения id процесса
     * @return id процесса
     */
    public int getId() {
        return id;
    }

    /**
     * Метод получения следующего значения nextIdProcess
     * @return 
     */
    public static int getNextIdProcess() {
        nextIdProcess++;
        return nextIdProcess;
    }

    /**
     * Метод получения следующего id потока
     * @return 
     */
    public int getNextIdThread() {
        nextIdThread++;
        return nextIdThread;
    }
    
    public static void setActualIdProcess(int id){
        nextIdProcess = id;
    }
    
    /**
     * Уменьшение значение id следующего процесс на 1
     */
    public static void decrementNextIdThread(){
        nextIdProcess--;
    }
    
   
    
    @Override
    public String toString() {
        return "Process: " + " id = " + String.format("%06d", id) + ", arraySize = " + arraySize;
    }

}

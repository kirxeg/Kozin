package managementprocessthread;

import java.io.Serializable;

/**
 *
 * @author Admin
 */
public class Thread implements Serializable{
    
    private static final long serialVersionUID = 1L; 
    
    private int id;//Айди потока
    private float workTime;//Время работы

    public Thread(){}

    public Thread(int id, float workTime) {
        this.id = id;
        this.workTime = workTime;
    }//Конструктор

    public int getId() {
        return id;
    }

    public float getWorkTime() {
        return workTime;
    }

    public void setWorkTime(float workTime) {
        this.workTime = workTime;
    }

    @Override
    public String toString() {
        return "Thread: " + "id = " + String.format("%06d", id) + ", workTime = " + workTime;
    }


}

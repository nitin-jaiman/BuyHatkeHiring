package ixigo.nitin.com.buyhatkehiring.Modal;

/**
 * Created by apple on 26/02/17.
 */

public class Message {

    private Long id;
    private String address;
    private String message;
    private Integer readState;
    private Long time;
    private String folderName;

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", address='" + address + '\'' +
                ", message='" + message + '\'' +
                ", readState='" + readState + '\'' +
                ", time='" + time + '\'' +
                ", folderName='" + folderName + '\'' +
                '}';
    }


    public String getAddress() {
        return address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getReadState() {
        return readState;
    }

    public void setReadState(Integer readState) {
        this.readState = readState;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}

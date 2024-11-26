package dk.easv.mytunes.exceptions;

public class DBException extends RuntimeException {

  public DBException (Exception e){
    super(e);
  }

  public DBException(String message) {
    super(message);
  }
}

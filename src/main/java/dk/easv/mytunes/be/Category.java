package dk.easv.mytunes.be;

public class Category {

    private int id;
    private String category;

    public Category(int id, String category){
        this.id = id;
        this.category = category;
    }

    public int getId(){
        return id;
    }

    public String getCategory(){
        return category;
    }
}

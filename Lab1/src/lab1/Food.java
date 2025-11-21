package lab1;

public abstract class Food implements Consumable, Nutritious {

    private String name;
    protected String size;

    public Food (String name, String size) {
        this.name = name;
        this.size = size;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size){
        this.size = size;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return size + " " + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof Food)) return false;

        Food food = (Food) obj;
        return name.equals(food.name) && size.equals(food.size);
    }


}

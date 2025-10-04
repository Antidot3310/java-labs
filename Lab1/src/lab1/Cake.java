package lab1;

// Cake - Food with 1 filling
public class Cake extends Food{

    private String fill1;

    public Cake(String fill1, String size){
        super("Cake", size);
        this.fill1 = fill1;
    }

    public String getComponents() {
        return fill1;
    }

    public void setComponents(String comp1){
        this.fill1 = comp1;
    }


    public void consume() {
        System.out.println(this + " is eaten");
    }

    public int calculateCalories() {
        int calories = switch (size.toLowerCase()) {
            case "small" -> 50;
            case "medium" -> 100;
            case "large" -> 200;
            default -> 0;
        };

        if (fill1 != null) calories += 100;
        return calories;
    }

    @Override
    public String toString(){
        return super.toString() + " with " + fill1;
    }

    @Override
    public boolean equals (Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof Dessert)) return false;

        Cake cake = (Cake) obj;
        return fill1.equals(cake.fill1);
    }

}

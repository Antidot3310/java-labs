package lab1;

import java.util.List;

// Dessert is food, that include 2 fillings
public class Dessert extends Food {

    // fillings of dessert
    private String fill1;
    private String fill2;


    public Dessert(String fill1, String fill2, String size){
        super("Dessert", size);
        this.fill1 = fill1;
        this.fill2 = fill2;
    }

    public List<String> getComponents() {
        return List.of(fill1, fill2);
    }

    public void setComponents (String comp1, String comp2){
        this.fill1 = comp1;
        this.fill2 = comp2;
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
        // add fillings calories
        if (fill1 != null) calories += 100;
        if (fill2 != null) calories += 100;

        return calories;
    }

    @Override
    public String toString(){
        return super.toString() + " with " + fill1 + " and " + fill2;
    }

   @Override
   public boolean equals (Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof Dessert)) return false;

        Dessert dessert = (Dessert) obj;
        return fill1.equals(dessert.fill1) && fill2.equals(dessert.fill2);
    }

}

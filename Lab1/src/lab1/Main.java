package lab1;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) {

        // max number of items in breakfast - 20
        Food[] breakfast = new Food[20];

        // iterator for breakfast
        int foodNum = 0;

        // flags
        boolean needToSort = false;
        boolean needToCalculateCalories = false;

        // Read products from CLI arguments
        for (String arg : args){

            // read flags
            if(arg.startsWith("-")){
                if (arg.equals("-sort")){
                    needToSort = true;
                    continue;
                }
                if (arg.equals("-calories")){
                    needToCalculateCalories = true;
                    continue;
                }
            }

            String[] parts = arg.split("/");
            String className = parts[0];

            Food newFood;

            // try to create object basing on class name
            try{

                Class<?> classObj = Class.forName("lab1." + className);

                //check number of parameters (first parameter - class name)
                Constructor<?> constructor;
                newFood = switch (parts.length) {
                    case 3 -> {
                        constructor = classObj.getConstructor(String.class, String.class);
                        yield (Food) constructor.newInstance(parts[1], parts[2]);
                    }
                    case 4 -> {
                        constructor = classObj.getConstructor(String.class, String.class, String.class);
                        yield (Food) constructor.newInstance(parts[1], parts[2], parts[3]);
                    }
                    default -> throw new NoSuchMethodException();
                };

                breakfast[foodNum] = newFood;
                foodNum++;
            }
            catch(ClassNotFoundException e) {
                System.out.println("Class - " + className + " Not found. Product hadn't added to breakfast");
            }
            catch (NoSuchMethodException e) {
                System.out.println("Necessary constructor with this mount of parameter hadn't found for " + className);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }

        // eat breakfast
        for (Food food : breakfast){
            if (food != null) {
                food.consume();
            }
        }

        // count calories if needed
        if (needToCalculateCalories){
            int totalCalories = 0;
            for (Food food : breakfast){
                if (food != null) {
                    totalCalories += food.calculateCalories();
                }
            }
            System.out.println("Breakfast include " + totalCalories + " calories");
        }

        // sort products by mount of parameter
        if (needToSort) Arrays.sort(breakfast, new Comparator<Food>() {
            @Override
            public int compare(Food f1, Food f2) {
                if (f1 == null) return 1;
                if (f2 == null) return -1;

                int f1ParameterNumber = 0;
                int f2ParameterNumber = 0;

                if (f1 instanceof Dessert) f1ParameterNumber = 2;
                else if (f1 instanceof Cake) f1ParameterNumber = 1;
                if (f2 instanceof Dessert) f1ParameterNumber = 2;
                else if (f2 instanceof Cake) f1ParameterNumber = 1;

                return Integer.compare(f2ParameterNumber, f1ParameterNumber);
            }
        });

//        for (Food food : breakfast){
//            if (food != null) {
//                System.out.println(food.toString());
//            }
//        }

    }
}
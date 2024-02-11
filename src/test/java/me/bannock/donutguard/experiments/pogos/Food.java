package me.bannock.donutguard.experiments.pogos;

public class Food implements Person {

    private String name;

    public Food(String name) {
        this.name = name;
    }

    public void eat(){
        System.out.println("I eat-a the food");
    }

    @Override
    public int getHealth() {
        return 0;
    }
}

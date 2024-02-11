package me.bannock.donutguard.experiments.pogos;

public interface Animal {

    void makeNoise();

    default void test(){
        System.out.println("asdf");
    }

}

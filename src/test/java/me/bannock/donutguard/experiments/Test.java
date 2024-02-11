package me.bannock.donutguard.experiments;

import me.bannock.donutguard.experiments.pogos.Animal;
import me.bannock.donutguard.experiments.pogos.Bannock;

public class Test {

    private String[] test;

    private void test(){
        test = new String[0];
        Animal animal = new Bannock();
        animal.test();
        Bannock bannock = new Bannock();
        bannock.test();
    }

}

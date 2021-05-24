package com.iulyus01.mazegenerator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class Info {


    public enum AlgState {
        WAITING, RUNNING, PAUSED
    }

    public static class Pair<A, B> {
        public A first;
        public B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }
    public static class Triple<A, B, C> {
        public A first;
        public B second;
        public C third;

        public Triple(A first, B second, C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }
    public static class Quadruple<A, B, C, D> {
        public A first;
        public B second;
        public C third;
        public D forth;

        public Quadruple(A first, B second, C third, D forth) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.forth = forth;
        }
    }

    public static String applicationTitle = "Maze Generator";

    public static int W = Gdx.graphics.getWidth();
    public static int H = Gdx.graphics.getHeight();

    public static Color colorWhite;
    public static Color colorGrey;
    public static Color colorGreyLighten5;
    public static Color colorGreen;
    public static Color colorCyan;
    public static Color colorCyanLighten4;
    public static Color colorCyanDarken2;
    public static Color colorTeal;
    public static Color colorTeal03;
    public static Color colorTeal005;
    public static Color colorBlueLighten5;
    public static Color colorTealDarken4;
    public static Color colorBlue;
    public static Color colorBlue03;
    public static Color colorBlueDarken4;
    public static Color colorLightBlue;
    public static Color colorIndigo;
    public static Color colorIndigo03;
    public static Color colorIndigo005;
    public static Color colorYellow;
    public static Color colorYellow03;
    public static Color colorYellow01;
    public static Color colorYellow005;
    public static Color colorRed;
    public static Color colorRed03;
    public static Color colorRed005;
    public static Color colorRedLighten5;
    public static Color colorPurple;
    public static Color colorPurple03;
    public static Color colorPurple008;
    public static Color colorPurple004;

    public static void init() {


        colorWhite = new Color(1, 1, 1, 1);
        colorGrey = new Color(.619f, .619f, .619f, 1);
        colorGreyLighten5 = new Color(.980f, .980f, .980f, 1);
        colorGreen = new Color(.180f, .8f, .443f, 1);
        colorCyan = new Color(.160f, .713f, .964f, 1);
        colorCyanLighten4 = new Color(.698f, .921f, .949f, 1);
        colorCyanDarken2 = new Color(0, .674f, .756f, 1);
        colorIndigo = new Color(.247f, .317f, .709f, 1);
        colorIndigo03 = new Color(.247f, .317f, .709f, .3f);
        colorIndigo005 = new Color(.247f, .317f, .709f, .05f);
        colorBlue = new Color(.129f, .588f, .952f, 1);
        colorBlue03 = new Color(.129f, .588f, .952f, .3f);
        colorBlueLighten5 = new Color(.89f, .949f, .992f, 1);
        colorBlueDarken4 = new Color(.05f, .278f, .631f, 1);
        colorLightBlue = new Color(.11f, .662f, .956f, 1);
        colorTeal = new Color(0, .588f, .533f, 1);
        colorTeal03 = new Color(0, .588f, .533f, .3f);
        colorTeal005 = new Color(0, .588f, .533f, .05f);
        colorTealDarken4 = new Color(0, .301f, .250f, 1);
        colorYellow = new Color(1, .921f, .231f, 1);
        colorYellow03 = new Color(1, .921f, .231f, .3f);
        colorYellow01 = new Color(1, .921f, .231f, .1f);
        colorYellow005 = new Color(1, .921f, .231f, .05f);
        colorRed = new Color(.937f, .325f, .313f, 1);
        colorRed03 = new Color(.937f, .325f, .313f, .3f);
        colorRed005 = new Color(.937f, .325f, .313f, .05f);
        colorRedLighten5 = new Color(1f, .921f, .933f, 1);
        colorPurple = new Color(.611f, .152f, .690f, 1);
        colorPurple03 = new Color(.611f, .152f, .690f, .3f);
        colorPurple008 = new Color(.611f, .152f, .690f, .08f);
        colorPurple004 = new Color(.611f, .152f, .690f, .04f);
    }

}

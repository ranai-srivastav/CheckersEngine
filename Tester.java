package edu.iastate.cs472.proj2;

import java.util.Arrays;

public class Tester
{
    public static void main(String[] args)
    {
        CheckersData cd = new CheckersData();
        System.out.println(cd);

        System.out.println(Arrays.toString(cd.getLegalMoves(CheckersData.RED)));

    }
}

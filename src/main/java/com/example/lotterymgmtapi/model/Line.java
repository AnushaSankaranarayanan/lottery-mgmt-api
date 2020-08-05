package com.example.lotterymgmtapi.model;


/**
 * The type Line.
 */
public class Line {
    private String numbers;
    private int result;

    /**
     * Instantiates a new Line.
     *
     * @param numbers the numbers
     * @param result  the result
     */
    public Line(String numbers, int result) {
        this.numbers = numbers;
        this.result = result;
    }

    /**
     * Gets numbers.
     *
     * @return the numbers
     */
    public String getNumbers() {
        return numbers;
    }

    /**
     * Sets numbers.
     *
     * @param numbers the numbers
     */
    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    /**
     * Gets result.
     *
     * @return the result
     */
    public int getResult() {
        return result;
    }

    /**
     * Sets result.
     *
     * @param result the result
     */
    public void setResult(int result) {
        this.result = result;
    }
}

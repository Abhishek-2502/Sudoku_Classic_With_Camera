package com.example.sudokuclassicwithcamera;

public class Prompts {

    public static final String sudoku_prompt = """
            Attached image is a sudoku grid. Extract digits from it and return a 9 by 9 matrix. if the space is empty, return 0.
    
            For example:
            [[1,2,4,0,9,7,0,6,3],
            [1,2,4,0,9,7,0,6,3],
            [1,2,4,0,9,7,0,6,3],
            [1,2,4,0,9,7,0,6,3],
            [1,2,4,0,9,7,0,6,3],
            [1,2,4,0,9,7,0,6,3],
            [1,2,4,0,9,7,0,6,3],
            [1,2,4,0,9,7,0,6,3],
            [1,2,4,0,9,7,0,6,3]]
    
            Analyze the attached image very carefully so that all the digits should be in their correct position.
    
            NOTE: Only return matrix, nothing else. If sudoku is not found in image, return **NULL**.
            """;
}

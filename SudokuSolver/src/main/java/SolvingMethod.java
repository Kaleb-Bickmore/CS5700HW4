import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class SolvingMethod {
    protected boolean twins;
    protected boolean guess;
    protected boolean onlyChoice;
    protected ArrayList<String[][]>SolvingStates = new ArrayList<String[][]>();

    public String[][] solve(String[][] board,HashMap<String,String> possibleValues, SudokuPuzzle myPuzzle) {

        String[][] solvedBoard = fillBoardWithPossibleValues(board.length, board, possibleValues,myPuzzle.getListOfIndexesTried());
        if(!SudokuChecker.checkNotEmpty(solvedBoard)){
            solvedBoard = fillWithDashes(solvedBoard.length,solvedBoard);
            return solvedBoard;
        }
        /**
         * check if this solved the sudoku puzzle
         */

        /**
         * Let the template method Begin
         */
        if(onlyChoice){
            myPuzzle.appendStrategy("OnlyChoice");

        }

        if (this.twins) {
            //System.out.println("twins");
            myPuzzle.appendStrategy("Twins");

            solvedBoard = useTwinsToElimnate(solvedBoard,possibleValues);
        }


        if (this.guess) {
            //System.out.println("guessing");
            myPuzzle.appendStrategy("Backtracking");
            solvedBoard = backTrack(solvedBoard,myPuzzle);
        }

        solvedBoard = fillWithDashes(solvedBoard.length,solvedBoard);
        return solvedBoard;

    }

    public String[][] useTwinsToElimnate(String[][] board,HashMap<String,String> possibleBoardValues) {
        int boardSize = board.length;
        int squareXStartingIndex = 0;
        int squareYStartingIndex = 0;
        int sizeOfSquare = (int)Math.sqrt(boardSize);
        for(int i = 0; i <boardSize;i++){
            if(i%sizeOfSquare == 0){
                squareXStartingIndex = i;
            }
            for (int j = 0; j< boardSize;j++){
                if(j%sizeOfSquare == 0){
                    squareYStartingIndex = j;
                }
                if(board[i][j].split("-").length==2){
                    findTwins(board,board[i][j],squareXStartingIndex,squareYStartingIndex,sizeOfSquare,i,j);

                }

            }
        }

        return board;

    }

    protected  void findTwins( String[][] board, String firstTwin, int squareXStartingIndex, int squareYStartingIndex,
                                int sizeOfSquare, int m , int n){
        String[] myRow = board[m];
        String [] myCol = new String[board.length];
        for(int k = 0; k < board.length;k++){
            myCol[k] = board[k][n];
        }
        String [] splitString =firstTwin.split("-");
        String backwardsTwin = splitString[1]+"-"+splitString[0];

        for(int i = 0; i <myRow.length;i++){
            if((myRow[i].equals(firstTwin)|| myRow[i].equals(backwardsTwin))&&i!= n){
                twinEliminateRow(m,n,m,i,board,splitString);
                return;
            }
            if((myCol[i].equals(firstTwin)|| myCol[i].equals(backwardsTwin))&&i!= m){
                twinEliminateCol(m,n,i,n,board,splitString);
                return;
            }
        }
        for(int k = squareXStartingIndex;k<sizeOfSquare;k++){
            for(int l = squareYStartingIndex;l<sizeOfSquare;l++){
                if((board[k][l].equals(firstTwin)|| board[k][l].equals(backwardsTwin))&&(m!= k||n!=l)){
                    twinEliminateSquare(m,n,k,l,board,splitString,squareXStartingIndex,squareYStartingIndex,sizeOfSquare);
                    return;
                }
                }
            }

    }

    protected void twinEliminateSquare(int i, int j, int twinI, int twinJ, String[][] board, String[] splitString,
                                     int squareXStartingIndex, int squareYStartingIndex, int sizeOfSquare) {

        for (int k = squareXStartingIndex; k <squareXStartingIndex+ sizeOfSquare; k++) {
            for (int l = squareYStartingIndex; l <squareYStartingIndex+ sizeOfSquare; l++) {
                if (!(i == k && j == l) && !(twinI == k && twinJ == l)) {
                    String[] temp = board[k][l].split("-");
                    String previousBoardChar = "";
                    board[k][l] = "";
                    for (int n = 0; n < temp.length; n++) {
                        if (!temp[n].equals(splitString[0]) && !temp[n].equals(splitString[1])) {
                            previousBoardChar = board[k][l];
                            board[k][l] = temp[n];
                            if (!previousBoardChar.equals("")) {
                                board[k][l] = board[k][l] + "-" + previousBoardChar;
                            }
                        }
                    }
                }
            }
        }
    }

    protected void twinEliminateRow(int i, int j, int twinI, int twinJ, String[][] board, String[] firstTwin){
        for (int k = 0;k<board.length;k++) {
            if(k!=j&&k!=twinJ) {
                String[] temp = board[i][k].split("-");
                String previousBoardChar = "";
                board[i][k]="";
                for(int l = 0;l<temp.length;l++) {
                    if (!temp[l].equals(firstTwin[0]) && !temp[l].equals(firstTwin[1])) {
                        previousBoardChar = board[i][k];
                        board[i][k] = temp[l];
                        if (!previousBoardChar.equals("")) {
                            board[i][k] = board[i][k] + "-" + previousBoardChar;
                        }
                    }
                }
            }
        }
    }
    protected void twinEliminateCol(int i, int j, int twinI, int twinJ, String[][] board, String[] firstTwin){
        for (int k = 0;k<board.length;k++) {
            if(k!=i&&k!=twinI) {
                String[] temp = board[k][j].split("-");
                String previousChar = "";
                board[k][j] = "";
                for(int l = 0;l<temp.length;l++) {
                    if (!temp[l].equals(firstTwin[0]) && !temp[l].equals(firstTwin[1])) {
                        previousChar =  board[k][j];
                        board[k][j] = temp[l];
                        if (!previousChar.equals("")) {
                            board[k][j] =  board[k][j] + "-" + previousChar;
                        }

                    }

                }
            }
        }
    }


    private String[][] backTrack(String[][] solvedBoard,SudokuPuzzle myPuzzle) {

        for(int i = 0;i<solvedBoard.length;i++) {
            for (int j = 0; j < solvedBoard.length; j++) {
                if (solvedBoard[i][j].split("-").length>1 ) {
                    String[] splitString = solvedBoard[i][j].split("-");
                    String [][] copySolvedBoard = new String[solvedBoard.length][solvedBoard.length];
                    for(int k = 0; k<solvedBoard.length;k++){
                        copySolvedBoard[k]=solvedBoard[k].clone();
                    }
                    copySolvedBoard = fillWithDashes(solvedBoard.length,copySolvedBoard);
                    myPuzzle.saveState(copySolvedBoard);
                    solvedBoard[i][j] = splitString [0];
                    myPuzzle.addToIndexesNotInBoard(i,j,splitString[0]);
                    return solvedBoard;

                }
            }
        }
        return solvedBoard;

    }


    public String[][] fillWithDashes(int length, String[][] solvedBoard){

        for(int i = 0;i<length;i++){
            for(int j =0 ;j<length;j++){
                if(solvedBoard[i][j].split("-").length>1){
                    solvedBoard[i][j]="-";
                }
            }
        }
        return solvedBoard;
    }

    protected String[][] fillBoardWithPossibleValues(int boardSize,String[][] board, HashMap<String, String> possibleBoardValues,String[][] guessedValues) {
        int squareXStartingIndex = 0;
        int squareYStartingIndex = 0;
        int sizeOfSquare = (int)Math.sqrt(boardSize);
        for(int i = 0; i <boardSize;i++){
            if(i%sizeOfSquare == 0){
                squareXStartingIndex = i;
            }
            for (int j = 0; j< boardSize;j++){
                if(j%sizeOfSquare == 0){
                    squareYStartingIndex = j;
                }
                if(board[i][j].equals("-")){
                    String[] myRow = board[i];
                    String [] myCol = new String[boardSize];
                    for(int k = 0; k < boardSize;k++){
                        myCol[k] = board[k][j];
                    }
                    board[i][j] = possibleValues(myRow,myCol,board,squareXStartingIndex,squareYStartingIndex,sizeOfSquare,possibleBoardValues,guessedValues[i][j]);

                }

            }
        }
        return board;
    }

    protected String possibleValues(String[] myRow, String[] myCol,String[][] board, int squareXStartingIndex,
                                             int squareYStartingIndex, int sizeOfSquare,HashMap<String, String> possibleBoardValues,String guessedValues){
        HashMap<String,Boolean> inBoardOrNot = new HashMap<String, Boolean>();
        for (String key:
             possibleBoardValues.keySet()) {
            if(!key.equals("-"))

                inBoardOrNot.put(key,Boolean.FALSE);
        }
        if(!guessedValues.equals("")) {
            for (String guessValue : guessedValues.split("-")) {
                inBoardOrNot.put(guessValue, Boolean.TRUE);

            }
        }
        for (int i = 0; i < myRow.length;i++) {
            if (!myRow[i].equals( "-") && myRow[i].split("-").length <= 1) {
                inBoardOrNot.put(myRow[i], Boolean.TRUE);
            }

            if (!myCol[i].equals( "-") || myCol[i].split("-").length <= 1) {
                    inBoardOrNot.put(myCol[i], Boolean.TRUE);

            }
        }
         for(int j = squareXStartingIndex;j<squareXStartingIndex+sizeOfSquare;j++){
             for(int k = squareYStartingIndex;k<squareYStartingIndex+sizeOfSquare;k++){
                if(!board[j][k].equals("-")|| board[j][k].split("-").length <= 1){
                    inBoardOrNot.put(board[j][k],Boolean.TRUE);

                }
             }
         }
         String cellValue = "";
        String previousCellValue = "";
        for (String key:
             inBoardOrNot.keySet()) {
            if(!inBoardOrNot.get(key)){
                previousCellValue = cellValue;
                cellValue = key;
                if(!previousCellValue.equals("")){
                    cellValue=cellValue+"-"+previousCellValue;
                }
            }

        }
        return cellValue;
    }

    public abstract String toString();

}

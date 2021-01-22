
public class Cell {

    private boolean isFlagged;
    private boolean isCovered;
    private int numNeighbors; 

    // constructor
    public Cell() {
        isFlagged = false;
        isCovered = true;
        numNeighbors = 0;
    }

    // flagging 
    public boolean isFlagged() {
        return isFlagged;
    }
    public void setFlagged(boolean x) {
        isFlagged = x;
    }

    // hidden status
    public boolean isCovered() {
        return isCovered;
    }
    public void uncover() {
        isCovered = false;
    }

    // number of neighboring mines
    public int getNumNeighbors() {
        return numNeighbors;
    }
    public void setNumNeighbors(int i) {
        numNeighbors = i;
    }

}

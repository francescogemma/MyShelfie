package it.polimi.ingsw.model;

import java.util.Stack;

/**
 * Fetches all the groups of adjacent shelves with the same color.
 */

// TODO: refactor this class

public class AdjacencyFetcher implements Fetcher {
    public enum ShelfStatus {
        NOT_VISITED,
        VISITED_NOT_COMPLETELY_EXPLORED,
        VISITED_COMPLETELY_EXPLORED,
        NOT_THE_SAME_COLOR
    }

    Stack<Shelf> stack;
    ShelfStatus[][] statuses = new ShelfStatus[Library.ROWS][Library.COLUMNS];


    public AdjacencyFetcher() {
        stack = new Stack<>();

        for(int i = 0; i < Library.ROWS; i++) {
            for(int j = 0; j < Library.COLUMNS; j++) {
                statuses[i][j] = ShelfStatus.NOT_VISITED;
            }
        }
    }

    @Override
    public Shelf next() {
        if(stack.isEmpty()) {
            stack.push(findAnotherShelf());
            statuses[stack.peek().getRow()][stack.peek().getColumn()] = ShelfStatus.VISITED_NOT_COMPLETELY_EXPLORED;
            return stack.peek();
        }

        Shelf current = stack.peek();

        if(canMoveInThisDirection(current, Offset.right())) {
            statuses[current.getRow()][current.getColumn() + 1] = ShelfStatus.VISITED_NOT_COMPLETELY_EXPLORED;
            stack.push(current.move(Offset.right()));
            return stack.peek();
        } else if(canMoveInThisDirection(current, Offset.down())) {
            statuses[current.getRow() + 1][current.getColumn()] = ShelfStatus.VISITED_NOT_COMPLETELY_EXPLORED;
            stack.push(current.move(Offset.down()));
            return stack.peek();
        } else if(canMoveInThisDirection(current, Offset.left())) {
            statuses[current.getRow()][current.getColumn() - 1] = ShelfStatus.VISITED_NOT_COMPLETELY_EXPLORED;
            stack.push(current.move(Offset.left()));
            return stack.peek();
        } else if (canMoveInThisDirection(current, Offset.up())) {
            statuses[current.getRow() - 1][current.getColumn()] = ShelfStatus.VISITED_NOT_COMPLETELY_EXPLORED;
            stack.push(current.move(Offset.up()));
            return stack.peek();
        } else {
            throw new IllegalStateException("There must always be a shelf to move to.");
        }
    }

    @Override
    public boolean lastShelf() {
        /*if(!stack.isEmpty()) {
            Shelf current = stack.peek();

            while(!stack.isEmpty() && cannotMove(current)) {
                current = stack.pop();
                statuses[current.getRow()][current.getColumn()] = ShelfStatus.VISITED_COMPLETELY_EXPLORED;
                if(!stack.isEmpty()) {
                    current = stack.peek();
                }
            }
        }*/
        while (!stack.isEmpty() && !canMove(stack.peek())) {
            statuses[stack.peek().getRow()][stack.peek().getColumn()] = ShelfStatus.VISITED_COMPLETELY_EXPLORED;
            stack.pop();
        }

        /*if(allCompletelyExplored() && stack.isEmpty()) {
            for (int i = 0; i < Library.ROWS; i++) {
                for (int j = 0; j < Library.COLUMNS; j++) {
                    statuses[i][j] = ShelfStatus.NOT_VISITED;
                }
            }
            return true;
        } else return stack.isEmpty();
         */

        if (allCompletelyExplored())  {
            for (int i = 0; i < Library.ROWS; i++) {
                for (int j = 0; j < Library.COLUMNS; j++) {
                    statuses[i][j] = ShelfStatus.NOT_VISITED;
                }
            }
        }

        if(stack.isEmpty()) {
            deleteAllNOT_THE_SAME_COLORShelves();
            return true;
        } else return false;
    }

    @Override
    public boolean hasFinished() {
        return allNotVisited() && stack.isEmpty();
    }

    // called when the next() shelf has not the same color of the current shelf
    @Override
    public boolean canFix() {
        if(stack.isEmpty()) {
            throw new IllegalStateException("The stack must not be empty when calling canFix");
        } else {
            Shelf toDelete = stack.pop();
            statuses[toDelete.getRow()][toDelete.getColumn()] = ShelfStatus.NOT_THE_SAME_COLOR;
        }
        return true;
    }

    private boolean canMove(Shelf shelf) {
        return canMoveInThisDirection(shelf, Offset.up()) ||
                canMoveInThisDirection(shelf, Offset.down()) ||
                canMoveInThisDirection(shelf, Offset.left()) ||
                canMoveInThisDirection(shelf, Offset.right());
    }

    private boolean canMoveInThisDirection(Shelf shelf, Offset offset) {
        if(shelf.getRow() == 0 && offset.getRowOffset() == -1) {
            return false;
        } else if(shelf.getRow() == Library.ROWS - 1 && offset.getRowOffset() == 1) {
            return false;
        } else if(shelf.getColumn() == 0 && offset.getColumnOffset() == -1) {
            return false;
        } else if(shelf.getColumn() == Library.COLUMNS - 1 && offset.getColumnOffset() == 1) {
            return false;
        } else {
            return (statuses[shelf.getRow() + offset.getRowOffset()][shelf.getColumn() + offset.getColumnOffset()] == ShelfStatus.NOT_VISITED);
        }
    }

    private boolean allExplored() {
        for(int i = 0; i < Library.ROWS; i++) {
            for(int j = 0; j < Library.COLUMNS; j++) {
                if(statuses[i][j] == ShelfStatus.NOT_VISITED) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean allCompletelyExplored() {
        for(int i = 0; i < Library.ROWS; i++) {
            for(int j = 0; j < Library.COLUMNS; j++) {
                if(statuses[i][j] != ShelfStatus.VISITED_COMPLETELY_EXPLORED) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean allNotVisited() {
        for(int i = 0; i < Library.ROWS; i++) {
            for(int j = 0; j < Library.COLUMNS; j++) {
                if(statuses[i][j] != ShelfStatus.NOT_VISITED) {
                    return false;
                }
            }
        }
        return true;
    }

    private Shelf findAnotherShelf() {
        for(int i = 0; i < Library.ROWS; i++) {
            for(int j = 0; j < Library.COLUMNS; j++) {
                if(statuses[i][j] == ShelfStatus.NOT_VISITED) {
                    return Shelf.getInstance(i, j);
                }
            }
        }
        return null;
    }

    private void deleteAllNOT_THE_SAME_COLORShelves() {
        for(int i = 0; i < Library.ROWS; i++) {
            for(int j = 0; j < Library.COLUMNS; j++) {
                if(statuses[i][j] == ShelfStatus.NOT_THE_SAME_COLOR) {
                    statuses[i][j] = ShelfStatus.NOT_VISITED;
                }
            }
        }
    }
}

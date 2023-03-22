package it.polimi.ingsw.model;

import java.util.Stack;

/**
 * Fetches all the groups of adjacent shelves with the same color.
 */

public class AdjacencyFetcher implements Fetcher {
    public enum ShelfStatus {
        NOT_VISITED,
        VISITED,
        NOT_THE_SAME_COLOR
    }

    Stack<Shelf> stack;
    ShelfStatus[][] statuses = new ShelfStatus[Library.ROWS][Library.COLUMNS];


    public AdjacencyFetcher() {
        stack = new Stack<>();

        setAllShelvesToNotVisited();
    }

    @Override
    public Shelf next() {
        if(stack.isEmpty()) {
            stack.push(findAnotherShelf());
            setShelfToVisited(stack.peek());
            return stack.peek();
        }

        Shelf current = stack.peek();

        if(canMoveInThisDirection(current, Offset.right())) {
            setShelfToVisited(current.move(Offset.right()));
            stack.push(current.move(Offset.right()));
            return stack.peek();
        } else if(canMoveInThisDirection(current, Offset.down())) {
            setShelfToVisited(current.move(Offset.down()));
            stack.push(current.move(Offset.down()));
            return stack.peek();
        } else if(canMoveInThisDirection(current, Offset.left())) {
            setShelfToVisited(current.move(Offset.left()));
            stack.push(current.move(Offset.left()));
            return stack.peek();
        } else if (canMoveInThisDirection(current, Offset.up())) {
            setShelfToVisited(current.move(Offset.up()));
            stack.push(current.move(Offset.up()));
            return stack.peek();
        } else {
            throw new IllegalStateException("There must always be a shelf to move to.");
        }
    }

    @Override
    public boolean lastShelf() {
        while (!stack.isEmpty() && !canMove(stack.peek())) {
            stack.pop();
        }

        if (areAllShelvesVisited())  {
            setAllShelvesToNotVisited();
        }

        if(stack.isEmpty()) {
            setupNewGroup();
            return true;
        } else return false;
    }

    @Override
    public boolean hasFinished() {
        return areAllShelvesNotVisited() && stack.isEmpty();
    }

    @Override
    public boolean canFix() {
        if(stack.isEmpty()) {
            throw new IllegalStateException("The stack must not be empty when calling canFix");
        } else {
            Shelf toDelete = stack.pop();
            setShelfToNotTheSameColor(toDelete);
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
        if(offset.getRowOffset() < -1 ||
                offset.getRowOffset() > 1 ||
                offset.getColumnOffset() < -1 ||
                offset.getColumnOffset() > 1 ||
                (offset.getRowOffset() == 0 && offset.getColumnOffset() == 0)) {
            throw new IllegalArgumentException("You can only move by exactly one shelf");
        } else if(shelf.getRow() == 0 && offset.getRowOffset() == -1) {
            return false;
        } else if(shelf.getRow() == Library.ROWS - 1 && offset.getRowOffset() == 1) {
            return false;
        } else if(shelf.getColumn() == 0 && offset.getColumnOffset() == -1) {
            return false;
        } else if(shelf.getColumn() == Library.COLUMNS - 1 && offset.getColumnOffset() == 1) {
            return false;
        } else {
            return (getShelfStatus(shelf.move(offset)) == ShelfStatus.NOT_VISITED);
        }
    }

    private boolean areAllShelvesVisited() {
        for(int i = 0; i < Library.ROWS; i++) {
            for(int j = 0; j < Library.COLUMNS; j++) {
                if(statuses[i][j] != ShelfStatus.VISITED) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean areAllShelvesNotVisited() {
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

    private void setupNewGroup() {
        for(int i = 0; i < Library.ROWS; i++) {
            for(int j = 0; j < Library.COLUMNS; j++) {
                if(statuses[i][j] == ShelfStatus.NOT_THE_SAME_COLOR) {
                    statuses[i][j] = ShelfStatus.NOT_VISITED;
                }
            }
        }
    }

    private void setAllShelvesToNotVisited() {
        for(int i = 0; i < Library.ROWS; i++) {
            for(int j = 0; j < Library.COLUMNS; j++) {
                statuses[i][j] = ShelfStatus.NOT_VISITED;
            }
        }
    }

    private void setShelfToNotTheSameColor(Shelf shelf) {
        statuses[shelf.getRow()][shelf.getColumn()] = ShelfStatus.NOT_THE_SAME_COLOR;
    }

    private void setShelfToVisited(Shelf shelf) {
        statuses[shelf.getRow()][shelf.getColumn()] = ShelfStatus.VISITED;
    }

    private ShelfStatus getShelfStatus(Shelf shelf) {
        return statuses[shelf.getRow()][shelf.getColumn()];
    }
}

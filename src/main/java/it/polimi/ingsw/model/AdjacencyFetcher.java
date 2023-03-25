package it.polimi.ingsw.model;

import java.util.Stack;

/**
 * Fetches all the groups of adjacent {@link Shelf shelves} with the same color.
 * It implements the {@link Fetcher} interface.
 * It uses a {@link Stack} to implement a DFS.
 *
 * @see Fetcher
 * @see Shelf
 *
 * @author Francesco Gemma
 */
public class AdjacencyFetcher implements Fetcher {
    /**
     * Represents the status of a {@link Shelf shelf}.
     * A status can be {@link ShelfStatus#NOT_VISITED}, {@link ShelfStatus#VISITED} or {@link ShelfStatus#NOT_THE_SAME_COLOR}.
     */
    private enum ShelfStatus {
        /**
         * It indicates that the shelf has not been visited yet.
         */
        NOT_VISITED,
        /**
         * It indicates that the shelf has been visited.
         */
        VISITED,
        /**
         * It indicates that the shelf has been visited,
         * but it is not the same color as the first shelf of the group.
         */
        NOT_THE_SAME_COLOR
    }

    // stack used to implement the DFS
    private final Stack<Shelf> stack;

    // matrix of the statuses of the shelves
    private final ShelfStatus[][] statuses = new ShelfStatus[Library.ROWS][Library.COLUMNS];


    /**
     * Constructor of the class.
     * It initializes the {@link Stack},
     * and it sets all the {@link Shelf shelves} to {@link ShelfStatus#NOT_VISITED}.
     */
    public AdjacencyFetcher() {
        stack = new Stack<>();

        setAllShelvesToNotVisited();
    }

    /*
     * The method is used to return another adjacent shelf that has not been visited yet.
     * If a group has been completed (the stack is empty),
     * the method starts to visit another group by picking another unvisited shelf.
     * This method should always return an adjacent shelf that has not been visited yet.
     * Return another adjacent shelf that has not been visited yet.
     * Throws IllegalStateException if the method cannot find another unvisited adjacent shelf to return.
     */
    @Override
    public Shelf next() {
        /*
         * if the stack is empty, it means that we are starting a new group,
         * so we find another not visited shelf and we push it on the stack
         * finally we set the shelf to VISITED and return it
         */
        if(stack.isEmpty()) {
            stack.push(findAnotherShelf());
            setShelfToVisited(stack.peek());
            return stack.peek();
        }

        /*
         * if the stack is not empty, we try to move in one of the four directions
         * using the method canMoveInThisDirection.
         * If we can move, we set the shelf to VISITED, we push it on the stack
         * and finally we return the shelf
         */
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
            // the next() method always return a shelf, so if we can't move in any direction we throw an exception
            throw new IllegalStateException("There must always be a shelf to move to.");
        }
    }

    /*
     * This method is used to check if the last shelf returned by next() is the last shelf of the group,
     * so it checks if a group of adjacent shelves with the same color has been completed.
     * If we cannot return any other shelf near the shelf on the top of the stack the method empties the stack until
     * it find, on the top of the stack, a shelf adjacent to another one that has not been visited yet.
     * It also checks if all groups have been fetched, and, if so, it sets all the shelves to NOT_VISITED,
     * so returns the fetching process to the initial state.
     * ready for the next computation.
     * Return true if the group has been completed, false otherwise.
     */
    @Override
    public boolean lastShelf() {
        /*
         * if the stack is not empty and we cannot move from the shelf on the top of the stack,
         * it means that we have to "turn back" (pop the stack) and search for another shelf adjacent to the group
         */
        while (!stack.isEmpty() && !canMove(stack.peek())) {
            stack.pop();
        }

        /*
         * check if all groups have been visited (by checking if all shelves are visited),
         * if so, set all shelves to NOT_VISITED (ready for the next fetch)
         */
        if (areAllShelvesVisited())  {
            setAllShelvesToNotVisited();
        }

        /*
         * if the stack is empty, it means that we have completed a group,
         * so we set all the NOT_THE_SAME_COLOR shelves to NOT_VISITED
         */
        if(stack.isEmpty()) {
            setupNewGroup();
            return true;
        } else return false;
    }

    /*
     * This method checks if the fetching process has finished, by checking if the statuses of all the shelves are NOT_VISITED
     * (if we are in the initial state).
     * Return true if the fetch has finished, and all the groups are completely visited, false otherwise.
     */
    @Override
    public boolean hasFinished() {
        /*
         * the process is finished if all shelves are not visited and if the stack is empty,
         * it checks if we are in the initial state
         */
        return areAllShelvesNotVisited() && stack.isEmpty();
    }

    /*
     * This method is called when the last shelf returned by next() was not with the same color of the group,
     * so it sets the last shelf to NOT_THE_SAME_COLOR and it pops it from the stack.
     * Return always true
     * Throws IllegalStateException if the stack is empty when calling this method, because it must mark the last shelf as NOT_THE_SAME_COLOR and pop it from the stack.
     */
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

    /**
     * This method checks if at least one of the {@link Shelf shelves} adjacent to the shelf passed as parameter is {@link ShelfStatus#NOT_VISITED}.
     * @param shelf the shelf from which we want to check if its adjacent shelves are not visited.
     * @return true if at least one of the adjacent shelves is not visited, false otherwise.
     */
    private boolean canMove(Shelf shelf) {
        return canMoveInThisDirection(shelf, Offset.up()) ||
                canMoveInThisDirection(shelf, Offset.down()) ||
                canMoveInThisDirection(shelf, Offset.left()) ||
                canMoveInThisDirection(shelf, Offset.right());
    }

    /**
     * This method checks if the {@link Shelf shelf} adjacent to the shelf passed as parameter in the direction specified by the {@link Offset offset}
     * is {@link ShelfStatus#NOT_VISITED}.
     * First it check if the shelf specified by the offset is not out of bounds, then it checks if the shelf is not visited.
     * @param shelf the shelf from which we want to check if one of its adjacent shelf is not visited.
     * @param offset the direction in which we want to check the adjacent shelf.
     * @return true if the adjacent shelf is not visited, false otherwise.
     * @throws IllegalArgumentException if the offset is not a valid offset, we can only check an adjacent shelf.
     */
    private boolean canMoveInThisDirection(Shelf shelf, Offset offset) {
        if(offset.getRowOffset() < -1 ||
                offset.getRowOffset() > 1 ||
                offset.getColumnOffset() < -1 ||
                offset.getColumnOffset() > 1 ||
                (offset.getRowOffset() == 0 && offset.getColumnOffset() == 0)) {
            // if the offset is not a valid offset, throw an exception
            throw new IllegalArgumentException("You can only check an adjacent shelf");
        } else if(shelf.getRow() == 0 && offset.getRowOffset() == -1) {
            // if the shelf is out of bounds, return false
            return false;
        } else if(shelf.getRow() == Library.ROWS - 1 && offset.getRowOffset() == 1) {
            return false;
        } else if(shelf.getColumn() == 0 && offset.getColumnOffset() == -1) {
            return false;
        } else if(shelf.getColumn() == Library.COLUMNS - 1 && offset.getColumnOffset() == 1) {
            return false;
        } else {
            // if the shelf is not out of bounds, check if it is not visited
            return (getShelfStatus(shelf.move(offset)) == ShelfStatus.NOT_VISITED);
        }
    }

    /**
     * @return true if the {@link ShelfStatus status} of all the {@link Shelf shelves} is {@link ShelfStatus#VISITED}, false otherwise.
     */
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

    /**
     * @return true if the {@link ShelfStatus status} of all the {@link Shelf shelves} is {@link ShelfStatus#NOT_VISITED}, false otherwise.
     */
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

    /**
     * This method is called when a new group is started, so it checks for a {@link ShelfStatus#NOT_VISITED} {@link Shelf shelf} for the next group.
     * @return the next shelf to visit so the next shelf not visited, null otherwise.
     */
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

    /**
     * This method sets all the {@link Shelf shelves} statuses from {@link ShelfStatus#NOT_THE_SAME_COLOR} to {@link ShelfStatus#NOT_VISITED}.
     * It is called when a new group is started.
     */
    private void setupNewGroup() {
        for(int i = 0; i < Library.ROWS; i++) {
            for(int j = 0; j < Library.COLUMNS; j++) {
                if(statuses[i][j] == ShelfStatus.NOT_THE_SAME_COLOR) {
                    statuses[i][j] = ShelfStatus.NOT_VISITED;
                }
            }
        }
    }

    /**
     * This method sets all the {@link Shelf shelves} statuses to {@link ShelfStatus#NOT_VISITED}.
     */
    private void setAllShelvesToNotVisited() {
        for(int i = 0; i < Library.ROWS; i++) {
            for(int j = 0; j < Library.COLUMNS; j++) {
                statuses[i][j] = ShelfStatus.NOT_VISITED;
            }
        }
    }

    /**
     * This method sets the {@link ShelfStatus status} of the {@link Shelf shelf} passed as parameter to {@link ShelfStatus#NOT_THE_SAME_COLOR}.
     * @param shelf the shelf to set to NOT_THE_SAME_COLOR.
     */
    private void setShelfToNotTheSameColor(Shelf shelf) {
        statuses[shelf.getRow()][shelf.getColumn()] = ShelfStatus.NOT_THE_SAME_COLOR;
    }

    /**
     * This method sets the {@link ShelfStatus status} of the {@link Shelf shelf} passed as parameter to {@link ShelfStatus#VISITED}.
     * @param shelf the shelf to set to VISITED.
     */
    private void setShelfToVisited(Shelf shelf) {
        statuses[shelf.getRow()][shelf.getColumn()] = ShelfStatus.VISITED;
    }

    /**
     * This method gets the {@link ShelfStatus status} of the {@link Shelf shelf} passed as parameter.
     * @param shelf the shelf to get the status from.
     * @return the status of the shelf passed as parameter.
     */
    private ShelfStatus getShelfStatus(Shelf shelf) {
        return statuses[shelf.getRow()][shelf.getColumn()];
    }
}

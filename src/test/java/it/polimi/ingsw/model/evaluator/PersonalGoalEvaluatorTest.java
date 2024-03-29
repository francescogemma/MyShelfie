package it.polimi.ingsw.model.evaluator;

import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.bookshelf.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

class PersonalGoalEvaluatorTest {
    PersonalGoalEvaluator personalGoalEvaluator;

    private static HashMap<Shelf, TileColor> getPersonalGoal() {
        HashMap<Shelf, TileColor> personalGoal = new HashMap<>();

        personalGoal.put(Shelf.getInstance(5, 4), TileColor.GREEN);
        personalGoal.put(Shelf.getInstance(2, 2), TileColor.YELLOW);
        personalGoal.put(Shelf.getInstance(3, 1), TileColor.BLUE);
        personalGoal.put(Shelf.getInstance(3, 2), TileColor.MAGENTA);
        personalGoal.put(Shelf.getInstance(4, 1), TileColor.WHITE);
        personalGoal.put(Shelf.getInstance(4, 3), TileColor.CYAN);

        return personalGoal;
    }

    @Test
    @DisplayName("Create blank evaluator, don't add anything, expect 0.")
    void getPoints_enoughMasks_correctOutput() {
        List<Integer> pointsMapping = List.of(3, 1, 4, 1, 5, 9);
        personalGoalEvaluator = new PersonalGoalEvaluator(getPersonalGoal(), pointsMapping);

        Assertions.assertEquals(0, personalGoalEvaluator.getPoints());
        Assertions.assertEquals(0, personalGoalEvaluator.getPointMasks().getSize());
    }

    @Test
    @DisplayName("Create blank evaluator, add enough to win, clear, get 0 again.")
    void clear_afterWinningCondition_correctOutput() {
        List<Integer> pointsMapping = List.of(3, 1, 4, 1, 5, 9);
        personalGoalEvaluator = new PersonalGoalEvaluator(getPersonalGoal(), pointsMapping);

        BookshelfMask bookshelfMask = new BookshelfMask(
                new MockBookshelf(new int[][]{
                    { 0, 0, 0, 0, 0 },
                    { 0, 0, 0, 0, 0 },
                    { 0, 0, 0, 0, 0 },
                    { 0, 0, 0, 0, 0 },
                    { 0, 0, 0, 0, 0 },
                    { 0, 0, 0, 0, TileColor.tileColorToIndex(TileColor.GREEN) }
            })
        );

        BookshelfMask pointMask = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 1 }
        });

        personalGoalEvaluator.add(bookshelfMask);
        Assertions.assertEquals(pointsMapping.get(0), personalGoalEvaluator.getPoints());
        Assertions.assertEquals(1, personalGoalEvaluator.getPointMasks().getSize());
        Assertions.assertEquals(pointMask, personalGoalEvaluator.getPointMasks().getBookshelfMasks().get(0));

        personalGoalEvaluator.clear();
        Assertions.assertEquals(0, personalGoalEvaluator.getPoints());
        Assertions.assertEquals(0, personalGoalEvaluator.getPointMasks().getSize());
    }

    @Test
    @DisplayName("Create blank evaluator, add enough to win each point amount element.")
    void getPoints_allWinningConditions_correctOutput() {
        List<Integer> pointsMapping = List.of(3, 1, 4, 2, 5, 9);
        personalGoalEvaluator = new PersonalGoalEvaluator(getPersonalGoal(), pointsMapping);

        // Points set 0
        BookshelfMask bookshelfMask = new BookshelfMask(
                new MockBookshelf(new int[][]{
                        { 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, TileColor.tileColorToIndex(TileColor.GREEN) }
                })
        );

        BookshelfMask pointMask = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 1 }
        });

        personalGoalEvaluator.add(bookshelfMask);
        Assertions.assertEquals(pointsMapping.get(0), personalGoalEvaluator.getPoints());
        Assertions.assertEquals(1, personalGoalEvaluator.getPointMasks().getSize());
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask));

        // Points set 1
        personalGoalEvaluator.clear();
        bookshelfMask = new BookshelfMask(
                new MockBookshelf(new int[][]{
                        { 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0 },
                        { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                        { 0, 0, TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                        { 0, 0, TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                        { 0, 0, TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
                })
        );

        BookshelfMask pointMask1 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 1, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 }
        });

        BookshelfMask pointMask2 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 1 }
        });

        personalGoalEvaluator.add(bookshelfMask);
        Assertions.assertEquals(pointsMapping.get(1), personalGoalEvaluator.getPoints());
        Assertions.assertEquals(2, personalGoalEvaluator.getPointMasks().getSize());
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask1));
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask2));

        // Points set 2
        personalGoalEvaluator.clear();
        bookshelfMask = new BookshelfMask(
                new MockBookshelf(new int[][]{
                        { 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0 },
                        { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                        { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                        { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                        { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
                })
        );

        pointMask1 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 1, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 }
        });

        pointMask2 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 }
        });

        BookshelfMask pointMask3 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 1 }
        });

        personalGoalEvaluator.add(bookshelfMask);
        Assertions.assertEquals(pointsMapping.get(2), personalGoalEvaluator.getPoints());
        Assertions.assertEquals(3, personalGoalEvaluator.getPointMasks().getSize());
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask1));
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask2));
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask3));

        // Points set 3
        personalGoalEvaluator.clear();
        bookshelfMask = new BookshelfMask(
                new MockBookshelf(new int[][]{
                        { 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0 },
                        { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                        { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                        { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                        { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
                })
        );

        pointMask1 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 1, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 }
        });

        pointMask2 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 }
        });

        pointMask3 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 1, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 }
        });

        BookshelfMask pointMask4 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 1 }
        });

        personalGoalEvaluator.add(bookshelfMask);
        Assertions.assertEquals(pointsMapping.get(3), personalGoalEvaluator.getPoints());
        Assertions.assertEquals(4, personalGoalEvaluator.getPointMasks().getSize());
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask1));
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask2));
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask3));
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask4));

        // Points set 4
        personalGoalEvaluator.clear();
        bookshelfMask = new BookshelfMask(
                new MockBookshelf(new int[][]{
                        { 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0 },
                        { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                        { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                        { 0, TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                        { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
                })
        );

        pointMask1 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 1, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 }
        });

        pointMask2 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 }
        });

        pointMask3 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 1, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 }
        });

        pointMask4 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0 }
        });

        BookshelfMask pointMask5 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), 0, TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 1 }
        });

        personalGoalEvaluator.add(bookshelfMask);
        Assertions.assertEquals(pointsMapping.get(4), personalGoalEvaluator.getPoints());
        Assertions.assertEquals(5, personalGoalEvaluator.getPointMasks().getSize());
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask1));
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask2));
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask3));
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask4));
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask5));

        // Points set 5
        personalGoalEvaluator.clear();
        bookshelfMask = new BookshelfMask(
                new MockBookshelf(new int[][]{
                        { 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0 },
                        { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                        { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                        { 0, TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.CYAN), 0 },
                        { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN) }
                })
        );

        pointMask1 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.CYAN), 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 1, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 }
        });

        pointMask2 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.CYAN), 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 }
        });

        pointMask3 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.CYAN), 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 1, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 }
        });

        pointMask4 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.CYAN), 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0 }
        });

        pointMask5 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.CYAN), 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 1 }
        });

        BookshelfMask pointMask6 = new MockBookshelfMask(new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, TileColor.tileColorToIndex(TileColor.YELLOW), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.BLUE), TileColor.tileColorToIndex(TileColor.MAGENTA), 0, 0 },
                { 0, TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.CYAN), 0 },
                { 0, TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.GREEN), TileColor.tileColorToIndex(TileColor.WHITE), TileColor.tileColorToIndex(TileColor.GREEN) }
        }), new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 1, 0 },
                { 0, 0, 0, 0, 0 }
        });

        personalGoalEvaluator.add(bookshelfMask);
        Assertions.assertEquals(pointsMapping.get(5), personalGoalEvaluator.getPoints());
        Assertions.assertEquals(6, personalGoalEvaluator.getPointMasks().getSize());
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask1));
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask2));
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask3));
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask4));
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask5));
        Assertions.assertTrue(personalGoalEvaluator.getPointMasks().getBookshelfMasks().contains(pointMask6));

        personalGoalEvaluator.clear();
        Assertions.assertEquals(0, personalGoalEvaluator.getPoints());
        Assertions.assertEquals(0, personalGoalEvaluator.getPointMasks().getSize());
    }

}

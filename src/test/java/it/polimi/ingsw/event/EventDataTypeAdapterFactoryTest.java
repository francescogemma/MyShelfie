package it.polimi.ingsw.event;

import it.polimi.ingsw.event.EventDataTypeAdapterFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class EventDataTypeAdapterFactoryTest {
    @Test
    void testNoDuplicateID () throws NoSuchFieldException, IllegalAccessException {
        EventDataTypeAdapterFactory eventDataTypeAdapterFactory = new EventDataTypeAdapterFactory();
        Field field = eventDataTypeAdapterFactory.getClass().getDeclaredField("EVENT_DATA_TYPES");
        field.setAccessible(true);
        Map<String, Type> map = new HashMap<>((Map<String, Type>)field.get(null));

        // check if there are any ID duplicate
        List<String> listId = new ArrayList<>();
        List<Type> listType = new ArrayList<>();

        map.forEach((id, type) -> {
            Assertions.assertNotNull(id);
            Assertions.assertFalse(id.isEmpty());
            listId.add(id);
            listType.add(type);
        });

        for (int i = 0; i < listId.size(); i++) {
            for (int j = 0; j < listId.size(); j++) {
                if (i != j) {
                    Assertions.assertNotEquals(
                            listId.get(i),
                            listId.get(j)
                    );
                }
            }
        }

        for (int i = 0; i < listId.size(); i++) {
            for (int j = 0; j < listId.size(); j++) {
                if (i != j) {
                    Assertions.assertNotEquals(
                            listType.get(i),
                            listType.get(j)
                    );
                }
            }
        }
    }
}

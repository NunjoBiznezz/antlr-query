package com.nunjobiznezz.rest.query;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryStringParserTest {

    private final QueryStringParser parser = new QueryStringParser();

    @Test
    public void testParsing() {

        {
            // Implicitly anded
            String parseInput = "(startTime >= 2025-01-07T05:00:00.000Z) (startTime <= 2025-01-08T05:00:00.000Z)";
//            String parseInput = "(startTime >= 2025-01-07T05:00:00.000Z)";

            var criteria = parser.parseCriteria(parseInput);
            assertNotNull(criteria);
//            assertEquals("startTime", criteria.getKey());
            System.out.println("OPERATOR = " + criteria.getCriteriaObject().toString());
        }

        {
            String parseInput = "startTime <= 2025-01-07T05:00:00.000Z";

            var criteria = parser.parseCriteria(parseInput);
            assertNotNull(criteria);
            assertEquals("startTime", criteria.getKey());
        }

        {
            String parseInput = "startTime >= 2025-01-07T05:11:12.013Z AND endTime <= 2025-01-08T05:00:00.000Z";

            var criteria = parser.parseCriteria(parseInput);
            assertNotNull(criteria);
//            System.out.println("OPERATOR = " + criteria.getCriteriaObject().toString());
        }

        {
            String parseInput = "startTime >= 2025-01-07T05:00:00.000Z AND endTime <= 2025-01-08T05:00:00.000Z";

            var criteria = parser.parseCriteria(parseInput);
            assertNotNull(criteria);
//            System.out.println("OPERATOR = " + criteria.getCriteriaObject().toString());
        }

        {
            String parseInput = "startTime >= 2025-01-07T05:00:00.000Z AND endTime <= 2025-01-08T05:00:00.000Z OR name = \"name of object\"";

            var criteria = parser.parseCriteria(parseInput);
            assertNotNull(criteria);
//            System.out.println("OPERATOR = " + criteria.getCriteriaObject().toString());
        }

        {
            String parseInput = "startTime >= 2025-01-07T05:00:00.000Z AND endTime <= 2025-01-08T05:00:00.000Z OR name = \"name of object\"";

            var criteria = parser.parseCriteria(parseInput);
            assertNotNull(criteria);
//            System.out.println("OPERATOR = " + criteria.getCriteriaObject().toString());
        }

        {
            String parseInput = "groupId = 678045979f1fe37a6621777c";

            var criteria = parser.parseCriteria(parseInput);
            assertNotNull(criteria);
//            System.out.println("OPERATOR = " + criteria.getCriteriaObject().toString());
        }
    }

    @Test
    public void testDateCriteria() {
        {
            String parseInput = "startTime >= 2025-01-07T05:00:00.000Z";

            var criteria = parser.parseCriteria(parseInput);
            assertNotNull(criteria);
//            System.out.println("OPERATOR = " + criteria.getCriteriaObject().toString());
        }
        {
            String parseInput = "startTime >= 2025-01-07T05:00:00.000Z";

            var criteria = parser.parseCriteria(parseInput);
            assertNotNull(criteria);
//            System.out.println("OPERATOR = " + criteria.getCriteriaObject().toString());
        }

        {
            String parseInput =
                    "(" +
                        "(startTime >= 2025-01-13T00:00:00.000Z)" +
                        " AND " +
                        "(startTime <= 2025-01-24T00:00:00.000Z)" +
                    ")" +
                    " OR " +
                    "(" +
                        "(endTime >= 2025-01-13T00:00:00.000Z)" +
                        " AND " +
                        "(endTime <= 2025-01-24T00:00:00.000Z)" +
                    ")";

            var criteria = parser.parseCriteria(parseInput);
            assertNotNull(criteria);
            System.out.println("OPERATOR = " + criteria.getCriteriaObject().toString());
        }
    }

    @Test
    public void testNullInput() {
        // Expect IllegalArgumentException to be thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parseCriteria(null); // Invalid input
        });

        // Optionally check the exception message
        assertEquals("Input cannot be null", exception.getMessage());
    }

    @Test
    public void testBadInput() {
        {
            String parseInput = "startTime >= 2025-01-07T05:00:00.000Z AND garbage input";

            // Expect IllegalArgumentException to be thrown
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                parser.parseCriteria(parseInput); // Invalid input
            });

            // Optionally check the exception message
            assertEquals("[line 1:50 no viable alternative at input 'garbageinput']", exception.getMessage());
        }

        {

            // Expect IllegalArgumentException to be thrown
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                parser.parseCriteria(""); // Invalid input
            });

            // Optionally check the exception message
            assertEquals("Input cannot be empty", exception.getMessage());
        }

        {
            String parseInput = "startTime >= 2025-01-07T05:00:00.000Z AND fieldName badComparator value more crap";

            // Expect IllegalArgumentException to be thrown
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                parser.parseCriteria(parseInput); // Invalid input
            });

            // Optionally check the exception message
            assertEquals("[line 1:52 no viable alternative at input 'fieldNamebadComparator']", exception.getMessage());
        }
    }

    @Test
    public void testInCriteria() {

        {
            String parseInput = "(fieldName IN [ 1, 2, 3, 4, 5 ])";

            var criteria = parser.parseCriteria(parseInput);
            assertNotNull(criteria);
//            System.out.println("OPERATOR = " + criteria.getCriteriaObject().toString());
        }

        {
            String parseInput = "(fieldName NIN [ 1, 2, 3, 4, 5 ])";

            var criteria = parser.parseCriteria(parseInput);
            assertNotNull(criteria);
//            System.out.println("OPERATOR = " + criteria.getCriteriaObject().toString());
        }

        {
            String parseInput =
                    "startTime >= 2025-01-07T05:00:00.000Z" +
                    " AND " +
                    "(fieldName IN [ 1, 2, 3, 4, 5 ])";

            var criteria = parser.parseCriteria(parseInput);
            assertNotNull(criteria);
//            System.out.println("OPERATOR = " + criteria.getCriteriaObject().toString());
        }

    }

    @Test
    public void testRegexCriteria() {
        {
            String parseInput = "givenName ~ /^doug/i";

            var criteria = parser.parseCriteria(parseInput);
            assertNotNull(criteria);
            System.out.println("OPERATOR = " + criteria.getCriteriaObject().toString());
        }

    }
}
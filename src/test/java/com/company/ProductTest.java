package com.company;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ProductTest {
    @Test
    void test1() {
        int year = 2010;
        int month = 3;
        int expected = 2;
        int result = Product.priceIndex(year, month);
        assumeTrue(expected == result);
    }

    @Test
    void shouldThrowException() {
        int year = 2000;
        int month = 4;
        assertThrows(IndexOutOfBoundsException.class, () -> {
            Product.priceIndex(year, month);
        });
    }

    private static Stream<Arguments> provideStringsForIsBlank() {
        return Stream.of(
                Arguments.of(3, 2010, 2),
                Arguments.of(12, 2012, 35),
                Arguments.of(5, 2020, 124)
        );
    }

    @ParameterizedTest
    @MethodSource("provideStringsForIsBlank")
    void test2(int month, int year, int expected) {
        assertEquals(expected, Product.priceIndex(year, month));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data.csv", numLinesToSkip = 1, delimiter = ';')
    void test3(int month, int year, int expected) {
        assertEquals(expected, Product.priceIndex(year, month));

    }
    @Nested
    class GetProductTest {
        @Test
        void test1(){
            int year = 2010;
            int month = 3;
            int expected = 2;
            int result = Product.priceIndex(year, month);
            assumeTrue(expected == result);
        }

        @Test
        void priceIndex() {
            int year = 2011;
            int month = 5;
            int expectedIndex = 16;

            assertEquals(expectedIndex, Product.priceIndex(year, month));
        }

        @Test
        void priceIndexThrowsException() {
            int year = 2000;
            int month = 1;

            assertThrows(IndexOutOfBoundsException.class, () -> Product.priceIndex(year, month));
        }


        private static Stream<Arguments> monthYearIndex() {
            return Stream.of(
                    arguments(2012, 12, 35),
                    arguments(2022, 3, 146),
                    arguments(2020, 5, 124)
            );
        }

        @ParameterizedTest
        @MethodSource("monthYearIndex")
        void priceIndex(int year, int month, int expectedIndex) {
            assertEquals(expectedIndex, Product.priceIndex(year, month));
        }

        @ParameterizedTest
        @CsvFileSource(files = "test/resources/months.csv")
        void priceIndex(String date, int year, int month, int expectedIndex) {
            assertEquals(expectedIndex, Product.priceIndex(year, month));
        }

        @Test
        void priceIndexMultiple() {
            int expectedIndex = 0;
            for(int year : IntStream.rangeClosed(2001, 2020).toArray()) {
                for(int month : IntStream.rangeClosed(1, 12).toArray()) {
                    assumeTrue(year>2010);
                    assertEquals(expectedIndex, Product.priceIndex(year, month));
                    expectedIndex++;
                }
            }
        }

        @Nested
        class GetProductTest {
            static Path tmpDir;
            private static void createFileStub(Path dir, String name) throws IOException {
                File file = File.createTempFile("product", ".csv", dir.toFile());//new (String.valueOf(dir.resolve(filename)));
                // file.deleteOnExit();
                FileWriter fileWriter = new FileWriter(file);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                printWriter.println(name);
                printWriter.println(); // linia z nagłówkiem
                printWriter.println(); // linia z cenami
                printWriter.close();

            }

            @BeforeAll
            static void populateProducts() throws IOException {
                tmpDir = Files.createTempDirectory("products");
                //tmpDir.toFile().deleteOnExit(); // nie działa, kiedy są pliki, nawet auto-usuwane

                System.out.println(tmpDir);

                createFileStub(tmpDir, "Aaa1");
                createFileStub(tmpDir, "Aaa2");
                createFileStub(tmpDir, "Aaa3x");
                createFileStub(tmpDir, "Bbb");

                Product.addProducts(FoodProduct::fromCsv, tmpDir);
            }

            @AfterAll
            static void cleanup() {
                for(File file : tmpDir.toFile().listFiles()) {
                    System.out.println(file + " deleted.");
                    file.delete();
                };
                tmpDir.toFile().delete();
            }

            @Test
            void getProductTest() throws AmbigiousProductException {
                Product product = Product.getProduct("B");
                assertEquals("Bbb", product.getName());
            }

            @TestFactory
            Stream<DynamicTest> dynamicGetProductTest() {
                List<String> input = Arrays.asList("Aaa1", "Aaa2", "Aaa3", "B");
                List<String> output = Arrays.asList("Aaa1", "Aaa2", "Aaa3x", "Bbb");

                return input.stream()
                        .map(prefix -> DynamicTest.dynamicTest(
                                "Test: "+prefix,
                                ()-> {
                                    int id = input.indexOf(prefix);
                                    assertEquals(output.get(id), Product.getProduct(prefix).getName());
                                }
                        ));
            }
        }
    }
}
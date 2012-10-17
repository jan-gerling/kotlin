/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * @author abreslav
 */
public class TuplesAndFunctionsGenerator {
    private static final int TUPLE_COUNT = 23;

    private static void generateTuples(PrintStream out, int count) {
        generated(out);
        for (int i = 1; i < count; i++) {
            out.print("public class Tuple" + i);
            out.print("<");
            for (int j = 1; j <= i; j++) {
                out.print("out T" + j);
                if (j < i) {
                    out.print(", ");
                }
            }
            out.print(">");
            out.print("(");
            for (int j = 1; j <= i; j++) {
                out.print("public val _" + j + ": " + "T" + j);
                if (j < i) {
                    out.print(", ");
                }
            }
            out.print(") {}");
            out.println();
        }
    }

    private static void generateFunctions(PrintStream out, int count, boolean extension) {
        generated(out);
        for (int i = 0; i < count; i++) {
            out.print("public abstract class " + (extension ? "ExtensionFunction" : "Function") + i);
            out.print("<");
            if (extension) {
                out.print("in T");
                if (count > 0) {
                    out.print(", ");
                }
            }
            for (int j = 1; j <= i; j++) {
                out.print("in P" + j);
                out.print(", ");
            }
            out.print("out R> {\n");
            out.print("  public abstract fun " + (extension ? "T." : "") +
                      "invoke(");
            for (int j = 1; j <= i; j++) {
                out.print("p" + j + ": " + "P" + j);
                if (j < i) {
                    out.print(", ");
                }
            }
            out.print(") : R\n");
            out.println("}");
        }
    }

    private static void generated(PrintStream out) {
        out.println("// Generated by " + TuplesAndFunctionsGenerator.class.getCanonicalName());
        out.println("// NOTE: this code is not loaded for JetStandardLibrary, but its equivalent is manually created in JetStandardClasses");
        out.println();
        out.println("package jet");
        out.println();
    }

    public static void main(String[] args) throws FileNotFoundException {
        File baseDir = new File("compiler/frontend/src/jet/");
        assert baseDir.exists() : "Base dir does not exist: " + baseDir.getAbsolutePath();

        PrintStream tuples = new PrintStream(new File(baseDir, "Tuples.jet"));
        generateTuples(tuples, TUPLE_COUNT);
        tuples.close();

        PrintStream functions = new PrintStream(new File(baseDir, "Functions.jet"));
        generateFunctions(functions, TUPLE_COUNT, false);
        functions.close();

        PrintStream extensionFunctions = new PrintStream(new File(baseDir, "ExtensionFunctions.jet"));
        generateFunctions(extensionFunctions, TUPLE_COUNT, true);
        extensionFunctions.close();
    }
}

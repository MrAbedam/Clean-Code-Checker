package ir.ac.kntu;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Scanner;


public class Main {
    ///setup:
    static int lineCounter = 1;

    static int orderCounter = 0;

    static int indentSpace = 0;

    static int switchIndent = -1;

    static int switchLine = -1;

    static boolean hasPackage = false;

    static boolean switchDefaultCheck = false;

    static boolean insideSwitch = false;

    static boolean isClean = true;

    static String lastOrder = "";

    public static int countSemis(String line) {
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ';') {
                count++;
            }
        }
        return count;
    }

    public static int countIndentation(String line) {
        int count = 0;
        if (line.isEmpty() == true) {
            return 0;
        }
        while (line.charAt(count) == ' ' || line.charAt(count) == '\t') {
            if (line.charAt(count) == ' ') {
                count++;
            } else {
                count += 8;
            }
        }
        return count;
    }

    public static void handlePackage(String line) {
        String packageRegex = "package .+;";
        String trimmedLine = line.trim();

        if (trimmedLine.matches(packageRegex)) {
            if (hasPackage == false) {
                if (orderCounter == 1) {
                    hasPackage = true;
                    lastOrder = "package";
                } else {
                    System.out.println("Error at line " + lineCounter + " ,Wrong place for package.");
                    isClean = false;
                }
            } else {
                System.out.println("Error at line " + lineCounter + " ,This program already has a package.");
                isClean = false;
            }
        } else {
            System.out.println("Error at line " + lineCounter + " ,Wrong package format.");
            isClean = false;
        }

    }

    public static void handleImport(String line) {
        String importRegex = "import .+;";
        String trimmedLine = line.trim();

        if (trimmedLine.matches(importRegex)) {
            if (lastOrder.equals("package") || lastOrder.equals("")) {
                int tmp = 0;
            } else {
                System.out.println("Error at line " + lineCounter + " ,Wrong place for import.");
                isClean = false;
            }
        } else {
            System.out.println("Error at line " + lineCounter + " ,Wrong import format.");
            isClean = false;
        }

    }

    public static int countBracketOpenIndent(String line) {
        int differ = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '{') {
                differ += 4;
            }
        }
        return differ;
    }

    public static int countBracketCloseIndent(String line) {
        int differ = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '}') {
                differ -= 4;
            }
        }
        return differ;
    }

    public static void handleClass(String line) {
        String classRegex = "public class Main{";
        String trimmedLine = line.trim();
        if (trimmedLine.equals(classRegex)) {
            lastOrder = "class";
        } else {
            System.out.println("Error at line " + lineCounter + " ,Wrong format for class.");
            isClean = false;
        }
    }

    public static void handleMethod(String line) {
        String returnType = "(byte|String|int|long|float|double|boolean|char|void)(\\[\\d*]|\\[\\d*])*";
        String lowerCamelCase = "([a-z]([a-z]|[0-9]|[A-Z]))([A-Z]|[a-z]|[0-9])*";
        String extraArg = "(, " + returnType + " " + lowerCamelCase + ")*";
        String methodRegex = "public static " + returnType + " " + lowerCamelCase + "\\(" + returnType + " " + lowerCamelCase + extraArg + "\\)\\{";
        String trimmedLine = line.trim();
        if (!trimmedLine.matches(methodRegex)) {
            System.out.println("Error at line " + lineCounter + " ,Wrong format for main method.");
            isClean = false;
        }
    }

    public static void handleIf(String line) {
        String ifRegex = "if\\(.+\\)\\{";
        String trimmedLine = line.trim();
        if (trimmedLine.matches(ifRegex) && trimmedLine.endsWith("{")) {
            lastOrder = "if";
        } else {
            System.out.println("Error at line " + lineCounter + " ,Wrong format for if command.");
            isClean = false;
        }
    }

    public static void handleElse(String line) {
        String elseRegex = "}else\\{$";
        String trimmedLine = line.trim();
        if (trimmedLine.matches(elseRegex)) {
            lastOrder = "else";
        } else {
            System.out.println("Error at line " + lineCounter + " ,Wrong format for else command.");
            isClean = false;
        }
    }

    public static void handleElseIf(String line) {
        String elseIfRegex = "}else if\\(.+\\)\\{";
        String trimmedLine = line.trim();
        if (trimmedLine.matches(elseIfRegex) && trimmedLine.endsWith("{")) {
            lastOrder = "else if";
        } else {
            System.out.println("Error at line " + lineCounter + " ,Wrong format for else if command.");
            isClean = false;
        }
    }

    public static void breakLongLines(String input) {
        if (input.length() > 80) {
            String[] words = input.split("(?<=[,+\\-*/=\"]\\s)|\\s+");
            int lineLength = 0;
            StringBuilder output = new StringBuilder();
            String indent = "";
            int startIndent = 0;
            while (input.trim().charAt(startIndent) != '=' && input.trim().charAt(startIndent) != '(' && input.trim().charAt(startIndent) != '"') {
                startIndent++;
            }
            for (int i = 0; i < startIndent; i++) {
                indent += " ";
            }

            for (int i = 0; i < words.length; i++) {
                if (lineLength + words[i].length() + indent.length() > 80) {
                    output.append("\n" + indent);
                    lineLength = 0;
                }
                output.append(words[i] + " ");
                lineLength += words[i].length() + 1;

            }
            System.out.println("Suggested way to break line:");
            System.out.println(output.toString());
        }
    }

    public static void handleLength(String line) {
        if (line.length() >= 80) {
            System.out.println("Error at line " + lineCounter + " ,More than 80 characters.");
            isClean = false;
            breakLongLines(line);
        }
    }

    public static void handleSingleBracket(String line) {
        String trimmedLine = line.trim();
        if (trimmedLine.equals("{")) {
            System.out.println("Error at line " + lineCounter + " ,bracket shouldn't be in a single line.");
            isClean = false;
        }
    }

    public static void handleIndentation(String line) {
        if (line.trim().isEmpty() || line.trim().equals("\n")) {
            int tmpp = 0;
        } else if (countIndentation(line) != indentSpace) {
            System.out.println("Error at line " + lineCounter + " ,Wrong indentation.");
            isClean = false;
        }
    }

    public static void handleSingleSemiColon(String line) {
        String trimmedLine = line.trim();
        if (trimmedLine.equals(";")) {
            System.out.println("Error at line " + lineCounter + " ,semicolon shouldn't be in a single line.");
            isClean = false;
        }
    }

    public static void handleMoreCommands(String line) {
        String trimmedLine = line.trim();
        if ((countSemis(line) == 1 && !trimmedLine.endsWith(";") && !line.contains("for")) || (countSemis(line) > 1 && !line.contains("for"))) {
            System.out.println("Error at line " + lineCounter + " ,More than 1 commands in one line.");
            isClean = false;
        }
    }

    public static void handleElseOrElseIfWrongIndent(String line) {
        System.out.println("Error at line " + lineCounter + " ,Bracket must be in the same line as else or else if.");
        isClean = false;
    }

    public static boolean declarationKeyWord(String line) {
        String trimmedLine = line.trim();
        if (trimmedLine.startsWith("String") || trimmedLine.startsWith("int") || trimmedLine.startsWith("boolean")) {
            return true;
        }
        if (trimmedLine.startsWith("long") || trimmedLine.startsWith("char") || trimmedLine.startsWith("double")) {
            return true;
        }
        if (trimmedLine.startsWith("float") || trimmedLine.startsWith("byte")) {
            return true;
        }
        return false;
    }

    public static void handleVariable(String line) {
        String trimmedLine = line.trim();
        String variableRegex = "(byte|String|int|long|float|double|boolean|char)(\\[\\d*]|\\[\\d*])* [a-z]([a-zA-Z0-9]+)( = .*)?;";
        if (!trimmedLine.matches(variableRegex)) {
            if (trimmedLine.matches("(byte|String|int|long|float|double|boolean|char)(\\[\\d*]|\\[\\d*])* [A-Z](.)*")) {
                System.out.println("Error at line " + lineCounter + " ,variable should be camelCase.");
                isClean = false;
            } else {
                System.out.println("Error at line " + lineCounter + " ,variable should at least be 2 characters, check spaces.");
                isClean = false;
            }
        }
    }

    public static void handleWhile(String line) {
        String trimmedLine = line.trim();
        String whileRegex = "while \\(.*\\)\\{";
        if (!trimmedLine.matches(whileRegex)) {
            System.out.println("Error at line " + lineCounter + " ,Wrong format for while.");
            isClean = false;
        }
    }

    public static void handleFor(String line) {
        String variableRegex = "(byte|String|int|long|float|double|boolean|char)(\\[\\d*]|\\[\\d*])* [a-z]([a-z]|[A-Z])*( = .*)?;";
        String trimmedLine = line.trim();
        String forRegex = "for\\(" + variableRegex + "(.)*" + "\\)\\{";
        if (!trimmedLine.matches(forRegex)) {
            System.out.println("Error at line " + lineCounter + " ,Wrong format for for.");
            isClean = false;
        }
    }

    public static void handleSwitch(String line) {
        insideSwitch = true;
        switchLine = lineCounter;
        switchIndent = indentSpace;
        switchDefaultCheck = false;
        String trimmedLine = line.trim();
        String switchRegex = "switch\\(.*\\)\\{";
        if (!trimmedLine.matches(switchRegex)) {
            System.out.println("Error at line " + lineCounter + " ,Wrong format for switch.");
            isClean = false;
        }
    }

    public static void handleCase(String line) {
        String trimmedLine = line.trim();
        String caseRegex = "case (.*): (.*)";
        if (!trimmedLine.matches(caseRegex)) {
            System.out.println("Error at line " + lineCounter + " ,Wrong format for case.");
            isClean = false;
        }
    }

    public static void handleDefault(String line) {
        String trimmedLine = line.trim();
        String defaultRegex = "default: (.*)";
        if (!trimmedLine.matches(defaultRegex)) {
            System.out.println("Error at line " + lineCounter + " ,Wrong format for default");
            isClean = false;
        }
        switchDefaultCheck = true;
    }

    public static void handleDefaultCheck(String line) {
        if (insideSwitch && indentSpace == switchIndent && lineCounter != switchLine) {
            if (!switchDefaultCheck) {
                System.out.println("Error at line " + switchLine + " ,This switch does not have default code.");
                isClean = false;
            }
            switchLine = -1;
            switchIndent = -1;
            insideSwitch = false;
            switchDefaultCheck = false;
        }
    }

    public static void hanleAllOrders(String line){
        String trimmedLine = line.trim();
        if (trimmedLine.startsWith("package")) {
            handlePackage(line);
        } else if (trimmedLine.startsWith("import")) {
            handleImport(line);
        } else if (trimmedLine.startsWith("public class")) {
            handleClass(line);
        } else if (trimmedLine.startsWith("public static")) {
            handleMethod(line);
        } else if (trimmedLine.startsWith("if")) {
            handleIf(line);
        } else if (trimmedLine.startsWith("}else if")) {
            handleElseIf(line);
        } else if (trimmedLine.startsWith("}else")) {
            handleElse(line);
        } else if (trimmedLine.startsWith("else") || trimmedLine.startsWith("else if")) {
            handleElseOrElseIfWrongIndent(line);
        } else if (declarationKeyWord(line)) {
            handleVariable(line);
        } else if (trimmedLine.startsWith("while")) {
            handleWhile(line);
        } else if (trimmedLine.startsWith("for")) {
            handleFor(line);
        } else if (trimmedLine.startsWith("switch")) {
            handleSwitch(line);
        } else if (trimmedLine.startsWith("case")) {
            handleCase(line);
        } else if (trimmedLine.startsWith("default")) {
            handleDefault(line);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String filename = scanner.nextLine();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("src/main/java/ir/ac/kntu/"+filename));
            String line = reader.readLine();
            while (line != null) {

                ///skip or end
                if (line == null) {
                    System.out.println("program ended");
                    break;
                }
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty()) {
                    orderCounter++;
                }
                line = line.replaceAll("\t", "    ");

                int openBracketIndent = countBracketOpenIndent(line);
                int closeBracketIndent = countBracketCloseIndent(line);

                indentSpace = indentSpace + closeBracketIndent;
                handleSingleBracket(line);
                handleSingleSemiColon(line);
                handleIndentation(line);
                handleMoreCommands(line);
                handleLength(line);
                hanleAllOrders(line);
                handleDefaultCheck(line);
                indentSpace = indentSpace + openBracketIndent;
                lineCounter++;
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isClean) {
            System.out.println("Your code doesn't stink!");
            makeSmileyFace();
        }
    }

    public static void makeSmileyFace(){
        JFrame frame = new JFrame("Clown Face");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Smiley());
        frame.setSize(300, 400);
        frame.setVisible(true);
    }
}

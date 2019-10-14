package plort.main;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import plort.core.ASTExecutor;
import plort.core.ASTTranslator;
import plort.core.PlortException;
import plort.core.ast.*;
import plort.core.value.NativeFuncRegistry;
import plort.gen.PlortLexer;
import plort.gen.PlortParser;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static plort.core.value.NativeFuncRegistry.id;
import static plort.core.value.NativeFuncRegistry.register;

public final class Main {
  
  // for lack of a better way to prevent construction
  private Main() {}
  
  public static final String ENTRY_NAME = "main";
  
  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.err.println("missing input file");
      System.exit(-1);
      return;
    }
    var defs = new ArrayList<VarDefBlob>();
    var errorLevel = 0;
    for (var arg : args) {
      var chars = CharStreams.fromFileName(arg, StandardCharsets.UTF_8);
      var lexer = new PlortLexer(chars);
      var tokens = new CommonTokenStream(lexer);
      var parser = new PlortParser(tokens);
      var file = parser.file();
      errorLevel += parser.getNumberOfSyntaxErrors();
      if (errorLevel == 0) defs.addAll(((VarDefsNode) file.accept(new ASTTranslator())).defs());
    }
    register(id("plort", "lang", "print"), 1, false, argv -> {
      var value = argv.get(1);
      System.out.print(value.stringValue());
      return value;
    });
    register(id("plort", "lang", "println"), 1, false, argv -> {
      var value = argv.get(0);
      System.out.println(value.stringValue());
      return value;
    });
    if (errorLevel == 0) try {
      new VarDefsNode(defs, new FuncCallNode(new VarNode(ENTRY_NAME), List.of())).accept(new ASTExecutor());
    } catch (PlortException e) {
      e.printStackTrace();
      errorLevel++;
    }
    System.exit(errorLevel);
  }
  
}

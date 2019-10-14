package plort.main;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import plort.core.ASTExecutor;
import plort.core.ASTTranslator;
import plort.core.ast.AST;
import plort.gen.PlortLexer;
import plort.gen.PlortParser;

import java.nio.charset.StandardCharsets;

public final class Main {
  
  // for lack of a better way to prevent construction
  private Main() {}
  
  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.err.println("missing input file");
      System.exit(-1);
      return;
    }
    var chars = CharStreams.fromFileName(args[0], StandardCharsets.UTF_8);
    var lexer = new PlortLexer(chars);
    var tokens = new CommonTokenStream(lexer);
    var parser = new PlortParser(tokens);
    var file = parser.file();
    if (parser.getNumberOfSyntaxErrors() == 0) ((AST.Node) file.accept(new ASTTranslator())).accept(new ASTExecutor());
  }
  
}

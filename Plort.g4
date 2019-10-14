grammar Plort;

file : def* EOF ;
def : LET ID (EQ | ARROW) expr ;
expr : control
     | expr WHERE varInit (COMMA varInit)*
     ;
varInit : ID (EQ | ARROW) expr ;
control : IF expr THEN expr (ELSE expr)?
        | WHILE (ID IN)? expr DO expr ON expr (ELSE expr)?
        | or
        ;
or : and (NOT? OR or)? ;
and : eq (NOT? AND and)? ;
eq : cmp (NOT? EQ eq)? ;
cmp : add ((NOT? (LT | GT | LTE | GTE) | CMP) cmp)? ;
add : mul ((ADD | SUB) add)? ;
mul : unary ((MUL | DIV | MOD) mul)? ;
unary : (ADD | SUB | NOT) unary
      | factor
      ;
factor : LPAREN expr RPAREN
       | ABS expr ABS
       | factor (LPAREN (expr (COMMA expr)* COMMA?)? RPAREN funcLit? | funcLit)
       | factor LBRACK expr RBRACK
       | factor MEMBER (ID | simpleLit)
       | factor ELLIPSIS
       | DO expr WHERE varInit (COMMA varInit)*
       | LET varInit (COMMA varInit)* IN expr
       | literal
       | ID
       ;
literal : funcLit
        | listLit
        | mapLit
        | simpleLit
        ;
funcLit : LAMBDA params DOT expr
        | LBRACE params ABS expr RBRACE
        ;
params : (ID (COMMA ID)* (COMMA ELLIPSIS ID)? | ELLIPSIS ID)? COMMA? ;
listLit : LBRACK (expr (COMMA expr)* COMMA?)? RBRACK ;
mapLit : LBRACE (member (COMMA member)* COMMA?)? RBRACE ;
member : (ID | simpleLit | LBRACK expr RBRACK) MEMBER expr
       | factor ELLIPSIS
       ;
simpleLit : STRING
          | NUMBER
          | TRUE | FALSE
          | NULL
          ;

LET : 'let' ;
WHERE : 'where' ;
IF : 'if' ;
WHILE : 'while' ;
THEN : 'then' ;
ELSE : 'else' ;
ON : 'on' ;
IN : 'in' ;
DO : 'do' ;
OR : 'or' ;
AND : 'and' ;
NOT : 'not' ;

ARROW : '->' ;
ELLIPSIS : '...' ;
EQ : '=' ;
CMP : '<>' ;
LTE : '<=' ;
GTE : '>=' ;
LT : '<' ;
GT : '>' ;

ADD : '+' ;
SUB : '-' ;
MUL : '*' ;
DIV : '/' ;
MOD : '%' ;
DOT : '.' ;
ABS : '|' ;
COMMA : ',' ;
MEMBER : ':' ;
LAMBDA : '\\' ;

LPAREN : '(' ;
RPAREN : ')' ;
LBRACK : '[' ;
RBRACK : ']' ;
LBRACE : '{' ;
RBRACE : '}' ;

TRUE : 'true' ;
FALSE : 'false' ;
NULL : 'null' ;
ID : [A-Za-z_'] [A-Za-z0-9_']* ;
NUMBER : [0-9]+ ('.' [0-9]+)? ;
STRING : '"' ('\\' ('\r\n' | .) | ~["])* '"'
       | '`' ('\\' ('\r\n' | .) | ~[`])* '`'
       ;

COMMENT : '/*' (~'*' | '*' ~'/')* '*/' -> skip ;
LINE_COMMENT : '//' ('\\' ('\r\n' | .) | ~[\r\n])* -> skip ;
WS : [ \t\r\n]+ -> skip ;
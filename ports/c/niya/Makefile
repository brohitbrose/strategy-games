#------------------------------------------------------------------------------
#
#  make                     makes test
#  make clean               removes all binaries
#  make memcheck            runs test under valgrind
#------------------------------------------------------------------------------

LIB_H		= niya.h
LIB_SRC		= state.c player.c game.c
LIB_OBJ		= state.o player.o game.o

TEST		= test
TEST_SRC	= $(TEST).c
TEST_OBJ	= $(TEST).o

COMPILE		= gcc -c -O3 -std=c99 -Wall
LINK		= gcc -o
MEMCHECK	= valgrind --leak-check=full


$(TEST) : $(TEST_OBJ)
	$(LINK) $(TEST) $(TEST_OBJ) $(LIB_OBJ)

$(TEST_OBJ) : $(LIB_H)
	$(COMPILE) $(LIB_SRC) $(TEST_SRC)

clean-obj :
	rm -f $(TEST_OBJ) $(CLI_OBJ) $(LIB_OBJ)

clean : clean-obj
	rm -f $(TEST)

memcheck : $(TEST)
	$(MEMCHECK) $(TEST)

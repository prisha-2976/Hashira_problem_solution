# Hashira_problem_solution
# SecretFinder Java Program

## This program reads polynomial roots from a JSON file, decodes the y-values given in different bases,
and solves for the secret constant 'c' of a polynomial using the matrix method.

## How it works?

1 Reads input JSON file (default: input.json).
2 For each root:
  - The key is the x-value.
  - The y-value is encoded in a base which can be 2, 4, 10, etc.
  - Decodes the y-value into decimal number.
3 Using the decoded points, it solves the polynomial coefficients assuming a quadratic polynomial (degree 2).
4 Uses the matrix method with Gaussian elimination to solve the system of equations.
  Matrix Method
We set up linear equations for the quadratic:
f(x)=ax^2+bx+c


For (1,4): 

a(1)^2+b(1)+c=4

For (2,7): 
a(2)^2+b(2)+c=7

For (3,12): 

a(3)^2+b(3)+c=12

Write as a matrix equation:
111    a  =  4
421    b  =  7
931    c   = 12
  
 solving get c=3


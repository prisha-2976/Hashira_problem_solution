# Hashira_problem_solution
# SecretFinder Java Program

## This program reads polynomial roots from a JSON file, decodes the y-values given in different bases,
and solves for the secret constant 'c' of a polynomial using the matrix method.

## How it works?

- Reads input JSON file (default: input.json).
- For each root:
  - The key is the x-value.
  - The y-value is encoded in a base which can be 2, 4, 10, etc.
  - Decodes the y-value into decimal number.
- Using the decoded points, it solves the polynomial coefficients assuming a quadratic polynomial (degree 2).
- Uses the matrix method with Gaussian elimination to solve the system of equations.
- Tries all combinations of k (minimum points needed) roots out of n.
- Prints the secret constant c from each combination and checks if all combinations agree.


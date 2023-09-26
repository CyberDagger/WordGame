# Input file containing a list of words
input_file = "src/input.txt"

# Output file where filtered words will be written
output_file = "src/output.txt"

# Function to filter words with 5 letters
def filter_words(word_list):
    return [word for word in word_list if len(word.strip()) == 5]

try:
    with open(input_file, "r") as infile, open(output_file, "w") as outfile:
        words = infile.read().split()  # Read words from input file

        five_letter_words = filter_words(words)  # Filter words with 5 letters

        # Write filtered words to the output file
        outfile.write("\n".join(five_letter_words))

    print("Filtered words with 5 letters have been written to", output_file)

except FileNotFoundError:
    print("Input file not found.")
except Exception as e:
    print("An error occurred:", str(e))

#!/bin/bash

# Define the command to execute the bytecode example
COMMAND="./../../../../../../openj9-openjdk-jdk8/build/linux-x86_64-normal-server-release/images/j2re-image/bin/java -Xint"

# Create an array to store the bytecode files
BYTECODES=("Test" "Test1" "Test2" "Test3" "ComplexDataExample" "ConditionalExample" "DeadCodeExample" "MethodCallExample" "NestedLoopExample")
#echo $BYTECODES
# Create arrays to store the execution times for optimized and non-optimized versions
OPTIMIZED_TIMES=()
NON_OPTIMIZED_TIMES=()
cd sootOutput
# Execute each bytecode file and measure the execution time
for bytecode in "${BYTECODES[@]}"; do
    # Execute optimized version
    optimized_time=$(TIMEFORMAT="%R"; { time $COMMAND "$bytecode"; } 2>&1)
    optimized_time=$(echo "$optimized_time * 1000" | bc -l) 2>&1  # Convert to milliseconds
    OPTIMIZED_TIMES+=("$optimized_time")
done
# Initialize an empty string variable
optimized=""
# Iterate over the elements of the array and concatenate them with commas
for ((i=0; i<${#OPTIMIZED_TIMES[@]}; i++)); do
    # Append the current element to the string
    optimized+="${OPTIMIZED_TIMES[i]}"
    # If it's not the last element, append a comma
    if [ $i -lt $((${#OPTIMIZED_TIMES[@]}-1)) ]; then
        optimized+=", "
    fi
done

# Print the resulting comma-separated string
#echo "$optimized"

cd ../testcases
for bytecode in "${BYTECODES[@]}"; do
    # Execute non-optimized version
    non_optimized_time=$(TIMEFORMAT="%R"; { time $COMMAND "$bytecode"; } 2>&1)
    non_optimized_time=$(echo "$non_optimized_time * 1000" | bc -l) 2>&1  # Convert to 
    NON_OPTIMIZED_TIMES+=("$non_optimized_time")
done
# Initialize an empty string variable
nonoptimized=""
# Iterate over the elements of the array and concatenate them with commas
for ((i=0; i<${#NON_OPTIMIZED_TIMES[@]}; i++)); do
    # Append the current element to the string
    nonoptimized+="${NON_OPTIMIZED_TIMES[i]}"
    # If it's not the last element, append a comma
    if [ $i -lt $((${#NON_OPTIMIZED_TIMES[@]}-1)) ]; then
        nonoptimized+=", "
    fi
done
cd ..
# Print the resulting comma-separated string
#echo "$nonoptimized"

echo ${OPTIMIZED_TIMES[@]}
echo ${NON_OPTIMIZED_TIMES[@]}
echo ${BYTECODES[@]}
# Plot a comparison graph for both versions using Python's matplotlib
python3 <<-EOF
import matplotlib.pyplot as plt

# Data
bytecode_labels = ["Test", "Test1", "Test2", "Test3", "DataStructure", "Conditional", "DeadCode", "MethodCall", "NestedLoop"]
optimized_times = [$optimized]
non_optimized_times = [$nonoptimized]

print(optimized_times, non_optimized_times)
# Plot
fig, ax = plt.subplots()
bar_width = 0.3
index = range(len(bytecode_labels))
bar1 = ax.bar(index, non_optimized_times, bar_width, label='Non-Optimized')
bar2 = ax.bar([i + bar_width for i in index], optimized_times, bar_width, label='Optimized')

# Add labels, title, and legend
ax.set_xlabel('Bytecode Example')
ax.set_ylabel('Execution Time (ms)')
ax.set_title('Execution Time Comparison')
ax.set_xticks([i + bar_width / 2 for i in index])
ax.set_xticklabels(bytecode_labels, fontsize=6)
ax.legend()

# Save the plot as an image
plt.savefig('comparison_graph.png')

EOF


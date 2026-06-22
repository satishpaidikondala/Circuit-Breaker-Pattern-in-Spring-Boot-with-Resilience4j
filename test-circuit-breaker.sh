#!/bin/bash

# Circuit Breaker Test Script
# This script demonstrates the Circuit Breaker pattern by:
# 1. Making calls to trip the circuit breaker
# 2. Verifying the circuit opens and fallback is returned
# 3. Waiting for the wait duration
# 4. Verifying the circuit closes again

BASE_URL="http://localhost:8080"
STATE_ENDPOINT="$BASE_URL/api/circuit-breaker/state"
USER_ENDPOINT="$BASE_URL/api/users/1"

echo "============================================"
echo " Circuit Breaker Pattern Test Script"
echo "============================================"
echo ""

# Function to print a separator
print_separator() {
    echo "--------------------------------------------"
}

# Function to get circuit breaker state
get_state() {
    curl -s "$STATE_ENDPOINT"
}

# Function to call user endpoint
call_user() {
    curl -s -w "\nHTTP Status: %{http_code}\n" "$USER_ENDPOINT"
}

echo "Step 1: Check initial circuit breaker state"
print_separator
STATE=$(get_state)
echo "Initial state: $STATE"
echo ""

echo "Step 2: Make calls to trip the circuit breaker"
echo "(The mock service fails on every 2nd request)"
print_separator
for i in $(seq 1 12); do
    echo "Call $i:"
    call_user
    echo ""
done

echo "Step 3: Check circuit breaker state after failures"
print_separator
STATE=$(get_state)
echo "Current state: $STATE"
if [ "$STATE" == "OPEN" ]; then
    echo "SUCCESS: Circuit breaker is OPEN as expected!"
else
    echo "NOTE: Circuit breaker is in $STATE state. Continue monitoring..."
fi
echo ""

echo "Step 4: Verify fallback response when circuit is open"
print_separator
echo "Making a call while circuit is open (should return fallback):"
call_user
echo ""

echo "Step 5: Wait for wait-duration-in-open-state (10 seconds)..."
print_separator
echo "Waiting..."
for i in $(seq 10 -1 1); do
    echo -ne "  $i seconds remaining...\r"
    sleep 1
done
echo -e "\nWait complete!"
echo ""

echo "Step 6: Check state after wait period"
print_separator
STATE=$(get_state)
echo "State after wait: $STATE"
echo ""

echo "Step 7: Make a successful call (should transition to half-open then closed)"
print_separator
echo "Calling user endpoint (may return fallback if still half-open):"
call_user
echo ""

echo "Step 8: Make more calls to fully close the circuit"
print_separator
for i in $(seq 1 5); do
    echo "Call $i:"
    call_user
    echo ""
done

echo "Step 9: Final circuit breaker state"
print_separator
STATE=$(get_state)
echo "Final state: $STATE"
echo ""

echo "Step 10: Check circuit breaker metrics"
print_separator
curl -s "$BASE_URL/api/circuit-breaker/metrics" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/api/circuit-breaker/metrics"
echo ""

echo "============================================"
echo " Test Complete!"
echo "============================================"

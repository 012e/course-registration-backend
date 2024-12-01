from datetime import timedelta
import re

def parse_duration(duration: str) -> timedelta:
    # Define regex pattern for matching duration components
    pattern = r'(?:(\d+)h)?(?:(\d+)m)?(?:(\d+)s)?|(\d+)'
    match = re.fullmatch(pattern, duration.strip())
    
    if not match:
        raise ValueError(f"Invalid duration format: {duration}")
    
    # Extract matched components
    hours = int(match.group(1) or 0)
    minutes = int(match.group(2) or 0)
    seconds = int(match.group(3) or 0)
    default_seconds = int(match.group(4) or 0)
    
    # If only default seconds were matched
    if default_seconds and not any([hours, minutes, seconds]):
        seconds = default_seconds

    return timedelta(hours=hours, minutes=minutes, seconds=seconds)

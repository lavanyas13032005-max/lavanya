import React, { useState, useEffect, useRef } from 'react';

export interface TimerProps {
  initialSeconds?: number;
}

export const Timer: React.FC<TimerProps> = ({ initialSeconds = 0 }) => {
  const [seconds, setSeconds] = useState<number>(initialSeconds);
  const [isRunning, setIsRunning] = useState<boolean>(false);
  const timerRef = useRef<number | null>(null);

  useEffect(() => {
    if (isRunning) {
      timerRef.current = window.setInterval(() => {
        setSeconds((prev) => prev + 1);
      }, 1000);
    } else if (timerRef.current !== null) {
      clearInterval(timerRef.current);
      timerRef.current = null;
    }

    return () => {
      if (timerRef.current !== null) {
        clearInterval(timerRef.current);
      }
    };
  }, [isRunning]);

  const handleStart = () => setIsRunning(true);
  const handlePause = () => setIsRunning(false);
  const handleReset = () => {
    setIsRunning(false);
    setSeconds(initialSeconds);
  };

  const formatTime = (totalSeconds: number): string => {
    const mins = Math.floor(totalSeconds / 60);
    const secs = totalSeconds % 60;
    return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
  };

  return (
    <div style={{ fontFamily: 'sans-serif', padding: '1rem', textAlign: 'center' }}>
      <h2 style={{ fontSize: '2rem', margin: '0.5rem 0' }}>{formatTime(seconds)}</h2>
      <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'center' }}>
        {!isRunning ? (
          <button type="button" onClick={handleStart} style={{ padding: '0.5rem 1rem' }}>
            Start
          </button>
        ) : (
          <button type="button" onClick={handlePause} style={{ padding: '0.5rem 1rem' }}>
            Pause
          </button>
        )}
        <button type="button" onClick={handleReset} style={{ padding: '0.5rem 1rem' }}>
          Reset
        </button>
      </div>
    </div>
  );
};

export default Timer;

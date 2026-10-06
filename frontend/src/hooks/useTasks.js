import { useState, useEffect } from 'react';
import { fetchTasks } from '../api';

export function useTasks(query, status, page, pageSize) {
  const [tasks, setTasks] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    let isCancelled = false;
    setLoading(true);
    setError(null);

    fetchTasks({ query, status, page, pageSize })
      .then((data) => {
        if (!isCancelled) {
          setTasks(data.items || []);
          setTotal(data.total || 0);
        }
      })
      .catch((err) => {
        if (!isCancelled) {
          setError(err.message || 'An error occurred while fetching tasks');
        }
      })
      .finally(() => {
        if (!isCancelled) {
          setLoading(false);
        }
      });

    return () => {
      isCancelled = true;
    };
  }, [query, status, page, pageSize]);

  return { tasks, total, loading, error };
}

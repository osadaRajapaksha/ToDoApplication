export const API_URL = process.env.NEXT_PUBLIC_API_URL || '/api';

export const getAuthHeaders = () => {
  if (typeof window !== 'undefined') {
    const user = JSON.parse(localStorage.getItem('user'));
    if (user && user.accessToken) {
      return { Authorization: 'Bearer ' + user.accessToken };
    }
  }
  return {};
};

export const fetchApi = async (endpoint, options = {}) => {
  const headers = {
    'Content-Type': 'application/json',
    ...getAuthHeaders(),
    ...(options.headers || {})
  };
  
  const response = await fetch(`${API_URL}${endpoint}`, {
    ...options,
    headers
  });
  
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    // Spring validation returns errors in different shapes
    const message =
      errorData.message ||
      (errorData.errors && errorData.errors[0]?.defaultMessage) ||
      (typeof errorData === 'string' ? errorData : null) ||
      `Request failed with status ${response.status}`;
    throw new Error(message);
  }
  
  if (response.status === 204 || response.headers.get('content-length') === '0') {
      return null;
  }

  const contentType = response.headers.get("content-type");
  if (contentType && contentType.indexOf("application/json") !== -1) {
    return response.json();
  } else {
    return response.text();
  }
};

export const breakdownTaskWithAI = async (title, description) => {
  return fetchApi('/ai/breakdown', {
    method: 'POST',
    body: JSON.stringify({ title, description })
  });
};

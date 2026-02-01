import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import UrlShortenerForm from './UrlShortenerForm';
import axios from 'axios';

jest.mock('axios');

describe('UrlShortenerForm', () => {
  const mockOnSuccess = jest.fn();
  const mockOnError = jest.fn();
  const apiBaseUrl = 'http://localhost:8080';

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders form elements', () => {
    render(
      <UrlShortenerForm
        apiBaseUrl={apiBaseUrl}
        onSuccess={mockOnSuccess}
        onError={mockOnError}
      />
    );

    expect(screen.getByPlaceholderText(/https:\/\/example.com/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/my-custom-alias/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Shorten URL/i })).toBeInTheDocument();
  });

  test('submits form with URL and custom alias', async () => {
    const mockResponse = {
      data: { shortUrl: 'http://localhost:8080/test-alias' }
    };
    axios.post.mockResolvedValue(mockResponse);

    render(
      <UrlShortenerForm
        apiBaseUrl={apiBaseUrl}
        onSuccess={mockOnSuccess}
        onError={mockOnError}
      />
    );

    const urlInput = screen.getByPlaceholderText(/https:\/\/example.com/i);
    const aliasInput = screen.getByPlaceholderText(/my-custom-alias/i);
    const submitButton = screen.getByRole('button', { name: /Shorten URL/i });

    fireEvent.change(urlInput, { target: { value: 'https://example.com/long/url' } });
    fireEvent.change(aliasInput, { target: { value: 'test-alias' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(axios.post).toHaveBeenCalledWith(
        `${apiBaseUrl}/shorten`,
        {
          fullUrl: 'https://example.com/long/url',
          customAlias: 'test-alias'
        }
      );
      expect(mockOnSuccess).toHaveBeenCalledWith('http://localhost:8080/test-alias');
    });
  });

  test('submits form with URL only (no custom alias)', async () => {
    const mockResponse = {
      data: { shortUrl: 'http://localhost:8080/abc123' }
    };
    axios.post.mockResolvedValue(mockResponse);

    render(
      <UrlShortenerForm
        apiBaseUrl={apiBaseUrl}
        onSuccess={mockOnSuccess}
        onError={mockOnError}
      />
    );

    const urlInput = screen.getByPlaceholderText(/https:\/\/example.com/i);
    const submitButton = screen.getByRole('button', { name: /Shorten URL/i });

    fireEvent.change(urlInput, { target: { value: 'https://example.com/long/url' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(axios.post).toHaveBeenCalledWith(
        `${apiBaseUrl}/shorten`,
        {
          fullUrl: 'https://example.com/long/url'
        }
      );
      expect(mockOnSuccess).toHaveBeenCalledWith('http://localhost:8080/abc123');
    });
  });

  test('displays error on API failure', async () => {
    const mockError = {
      response: {
        data: { error: 'Alias already exists' }
      }
    };
    axios.post.mockRejectedValue(mockError);

    render(
      <UrlShortenerForm
        apiBaseUrl={apiBaseUrl}
        onSuccess={mockOnSuccess}
        onError={mockOnError}
      />
    );

    const urlInput = screen.getByPlaceholderText(/https:\/\/example.com/i);
    const submitButton = screen.getByRole('button', { name: /Shorten URL/i });

    fireEvent.change(urlInput, { target: { value: 'https://example.com/long/url' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockOnError).toHaveBeenCalledWith('Alias already exists');
    });
  });

  test('prevents submission with empty URL', async () => {
    render(
      <UrlShortenerForm
        apiBaseUrl={apiBaseUrl}
        onSuccess={mockOnSuccess}
        onError={mockOnError}
      />
    );

    const submitButton = screen.getByRole('button', { name: /Shorten URL/i });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockOnError).toHaveBeenCalledWith('Please enter a URL');
      expect(axios.post).not.toHaveBeenCalled();
    });
  });
});


import React, { useState } from 'react';
import axios from 'axios';
import './UrlShortenerForm.css';

const UrlShortenerForm = ({ apiBaseUrl, onSuccess, onError }) => {
  const [fullUrl, setFullUrl] = useState('');
  const [customAlias, setCustomAlias] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!fullUrl) {
      onError('Please enter a URL');
      return;
    }

    setLoading(true);

    try {
      const payload = {
        fullUrl: fullUrl,
      };

      if (customAlias && customAlias.trim()) {
        payload.customAlias = customAlias.trim();
      }

      const response = await axios.post(`${apiBaseUrl}/shorten`, payload);

      onSuccess(response.data.shortUrl);
      setFullUrl('');
      setCustomAlias('');
    } catch (err) {
      console.error('Error shortening URL:', err);
      if (err.response?.data) {
        const errorData = err.response.data;
        if (errorData.error) {
          onError(errorData.error);
        } else if (errorData.fullUrl) {
          onError(errorData.fullUrl);
        } else if (errorData.customAlias) {
          onError(errorData.customAlias);
        } else {
          onError('Failed to shorten URL');
        }
      } else {
        onError('Failed to connect to server');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="govuk-form-group">
      <form onSubmit={handleSubmit}>
        <div className="govuk-form-group">
          <label className="govuk-label govuk-label--m" htmlFor="full-url">
            Enter URL to shorten
          </label>
          <div className="govuk-hint" id="full-url-hint">
            For example, https://example.com/very/long/url
          </div>
          <input
            className="govuk-input"
            id="full-url"
            name="fullUrl"
            type="text"
            aria-describedby="full-url-hint"
            value={fullUrl}
            onChange={(e) => setFullUrl(e.target.value)}
            disabled={loading}
          />
        </div>

        <div className="govuk-form-group">
          <label className="govuk-label govuk-label--m" htmlFor="custom-alias">
            Custom alias (optional)
          </label>
          <div className="govuk-hint" id="custom-alias-hint">
            Leave blank for a random alias. Only alphanumeric characters, hyphens, and underscores allowed.
          </div>
          <input
            className="govuk-input govuk-input--width-20"
            id="custom-alias"
            name="customAlias"
            type="text"
            aria-describedby="custom-alias-hint"
            value={customAlias}
            onChange={(e) => setCustomAlias(e.target.value)}
            disabled={loading}
          />
        </div>

        <button
          type="submit"
          className="govuk-button"
          data-module="govuk-button"
          disabled={loading}
        >
          {loading ? 'Shortening...' : 'Shorten URL'}
        </button>
      </form>
    </div>
  );
};

export default UrlShortenerForm;


import React, { useState, useEffect } from 'react';
import './App.css';
import UrlShortenerForm from './components/UrlShortenerForm';
import UrlList from './components/UrlList';
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

function App() {
  const [urls, setUrls] = useState([]);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const fetchUrls = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/urls`);
      setUrls(response.data);
    } catch (err) {
      console.error('Error fetching URLs:', err);
      setError('Failed to fetch URLs');
    }
  };

  useEffect(() => {
    fetchUrls();
  }, []);

  const handleUrlShortened = (shortUrl) => {
    setSuccess(`URL shortened successfully: ${shortUrl}`);
    setError(null);
    fetchUrls();
    setTimeout(() => setSuccess(null), 5000);
  };

  const handleError = (errorMessage) => {
    setError(errorMessage);
    setSuccess(null);
  };

  const handleDelete = async (alias) => {
    try {
      await axios.delete(`${API_BASE_URL}/${alias}`);
      setSuccess(`URL with alias "${alias}" deleted successfully`);
      setError(null);
      fetchUrls();
      setTimeout(() => setSuccess(null), 3000);
    } catch (err) {
      console.error('Error deleting URL:', err);
      setError(err.response?.data?.error || 'Failed to delete URL');
    }
  };

  return (
    <div className="govuk-width-container">
      <header className="govuk-header" role="banner" data-module="govuk-header">
        <div className="govuk-header__container govuk-width-container">
          <div className="govuk-header__logo">
            <span className="govuk-header__logotype">
              <span className="govuk-header__logotype-text">
                GOV.UK
              </span>
            </span>
          </div>
          <div className="govuk-header__content">
            <span className="govuk-header__link--service-name">
              URL Shortener
            </span>
          </div>
        </div>
      </header>

      <div className="govuk-width-container">
        <main className="govuk-main-wrapper" id="main-content" role="main">
          <div className="govuk-grid-row">
            <div className="govuk-grid-column-two-thirds">
              <h1 className="govuk-heading-xl">URL Shortener Service</h1>
              <p className="govuk-body-l">
                Transform long URLs into short, shareable links
              </p>
            </div>
          </div>

          {error && (
            <div className="govuk-error-summary" aria-labelledby="error-summary-title" role="alert" data-module="govuk-error-summary">
              <h2 className="govuk-error-summary__title" id="error-summary-title">
                There is a problem
              </h2>
              <div className="govuk-error-summary__body">
                <p>{error}</p>
              </div>
            </div>
          )}

          {success && (
            <div className="govuk-notification-banner govuk-notification-banner--success" role="alert" aria-labelledby="govuk-notification-banner-title" data-module="govuk-notification-banner">
              <div className="govuk-notification-banner__header">
                <h2 className="govuk-notification-banner__title" id="govuk-notification-banner-title">
                  Success
                </h2>
              </div>
              <div className="govuk-notification-banner__content">
                <p className="govuk-notification-banner__heading">
                  {success}
                </p>
              </div>
            </div>
          )}

          <div className="govuk-grid-row">
            <div className="govuk-grid-column-two-thirds">
              <UrlShortenerForm
                apiBaseUrl={API_BASE_URL}
                onSuccess={handleUrlShortened}
                onError={handleError}
              />
            </div>
          </div>

          <div className="govuk-grid-row" style={{ marginTop: '2rem' }}>
            <div className="govuk-grid-column-full">
              <UrlList
                urls={urls}
                onDelete={handleDelete}
              />
            </div>
          </div>
        </main>
      </div>

      <footer className="govuk-footer" role="contentinfo">
        <div className="govuk-width-container">
          <div className="govuk-footer__meta">
            <div className="govuk-footer__meta-item govuk-footer__meta-item--grow">
              <h2 className="govuk-visually-hidden">Support links</h2>
              <ul className="govuk-footer__inline-list">
                <li className="govuk-footer__inline-list-item">
                  Built with Spring Boot and React
                </li>
              </ul>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default App;

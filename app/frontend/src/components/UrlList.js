import React from 'react';
import './UrlList.css';

const UrlList = ({ urls, onDelete }) => {
  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text);
  };

  if (urls.length === 0) {
    return (
      <div className="govuk-inset-text">
        <p>No shortened URLs yet. Create your first shortened URL above to get started!</p>
      </div>
    );
  }

  return (
    <div>
      <h2 className="govuk-heading-l">
        Your shortened URLs ({urls.length})
      </h2>

      <table className="govuk-table">
        <thead className="govuk-table__head">
          <tr className="govuk-table__row">
            <th scope="col" className="govuk-table__header">Alias</th>
            <th scope="col" className="govuk-table__header">Short URL</th>
            <th scope="col" className="govuk-table__header">Original URL</th>
            <th scope="col" className="govuk-table__header">Actions</th>
          </tr>
        </thead>
        <tbody className="govuk-table__body">
          {urls.map((url) => (
            <tr key={url.alias} className="govuk-table__row">
              <td className="govuk-table__cell">
                <strong className="govuk-tag govuk-tag--blue">{url.alias}</strong>
              </td>
              <td className="govuk-table__cell">
                <a
                  href={url.shortUrl}
                  className="govuk-link"
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  {url.shortUrl}
                </a>
                <button
                  onClick={() => copyToClipboard(url.shortUrl)}
                  className="govuk-button govuk-button--secondary copy-button"
                  data-module="govuk-button"
                  title="Copy to clipboard"
                >
                  Copy
                </button>
              </td>
              <td className="govuk-table__cell url-cell">
                <span className="full-url" title={url.fullUrl}>
                  {url.fullUrl}
                </span>
              </td>
              <td className="govuk-table__cell">
                <button
                  onClick={() => {
                    if (window.confirm(`Are you sure you want to delete the alias "${url.alias}"?`)) {
                      onDelete(url.alias);
                    }
                  }}
                  className="govuk-button govuk-button--warning"
                  data-module="govuk-button"
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default UrlList;


import React from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash';
import { readAsText } from 'promise-file-reader';

import { postApiFile } from '../../api/post-api-file';
import { postApiLink } from '../../api/post-api-link';
import localization from '../../utils/localization';
import { ImportDialog } from './import-dialog/import-dialog.component';
import { ImportFileUpload } from './import-file-upload/import-file-upload.component';
import ImportLinkUpload from './import-link-upload/import-link-upload.component';

const redirectWhenImportSucceeded = (
  props,
  responseData,
  catalogId,
  importFrom
) => {
  if (_.get(responseData, 'id')) {
    props.history.push(
      `/catalogs/${catalogId}/apis/${_.get(
        responseData,
        'id'
      )}?importSuccess=${importFrom}`
    );
  }
};

export class ApiImportPage extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      catalogId: null,
      error: false,
      showImportByFile: false,
      fileName: null,
      showImportByLink: false,
      importUrl: null
    };
    this.showLinkImport = this.showLinkImport.bind(this);
    this.onCancelImport = this.onCancelImport.bind(this);
    this.onLinkUpload = this.onLinkUpload.bind(this);
    this.onChangeUrl = this.onChangeUrl.bind(this);
    this.onFileUpload = this.onFileUpload.bind(this);
  }

  componentWillMount() {
    const { match } = this.props;
    const catalogId = _.get(match, ['params', 'catalogId']);
    if (catalogId) {
      this.setState({
        catalogId
      });
    }
  }

  onCancelImport() {
    this.setState({
      error: false,
      fileName: null,
      showImportByFile: false,
      showImportByLink: false,
      importUrl: null
    });
  }

  onLinkUpload() {
    const { catalogId, importUrl } = this.state;
    const { addApiItem } = this.props;

    postApiLink(catalogId, importUrl)
      .then(responseData => {
        addApiItem(responseData);
        redirectWhenImportSucceeded(
          this.props,
          responseData,
          catalogId,
          importUrl
        );
      })
      .catch(error => {
        if (process.env.NODE_ENV !== 'production') {
          console.log('error', error); // eslint-disable-line no-console
        }
        this.setState({
          error: true
        });
      });
  }

  onChangeUrl(e) {
    this.setState({ [e.target.name]: e.target.value });
  }

  onFileUpload(e) {
    const { catalogId } = this.state;
    const { addApiItem } = this.props;
    const fileMetaData = e.target.files[0] || null;

    if (fileMetaData) {
      this.setState({
        fileName: _.get(fileMetaData, 'name')
      });
      readAsText(fileMetaData)
        .then(apiSpec => {
          postApiFile(catalogId, apiSpec)
            .then(responseData => {
              addApiItem(responseData);
              redirectWhenImportSucceeded(
                this.props,
                responseData,
                catalogId,
                _.get(fileMetaData, 'name')
              );
            })
            .catch(error => {
              if (process.env.NODE_ENV !== 'production') {
                console.log('error', error); // eslint-disable-line no-console
              }
              this.setState({
                error: true,
                showImportByFile: true
              });
            });
        })
        .catch(error => {
          if (process.env.NODE_ENV !== 'production') {
            console.log('error', error); // eslint-disable-line no-console
          }
          this.setState({
            error: true
          });
        });
    }
  }

  showLinkImport() {
    this.setState({
      error: false,
      showImportByLink: true,
      showImportByFile: false
    });
  }

  render() {
    const { error, showImportByFile, fileName, showImportByLink } = this.state;

    return (
      <div className="container">
        <div className="row mb-5">
          <div className="col-12">
            <h1>{localization.api.register.registerNew}</h1>
          </div>
        </div>

        <div className="row">
          <div className="col-12">
            {!error &&
              !showImportByLink &&
              !showImportByFile && (
                <ImportDialog
                  showLinkImport={this.showLinkImport}
                  handleFileUpload={this.onFileUpload}
                />
              )}

            {showImportByFile && (
              <ImportFileUpload
                fileName={fileName}
                handleFileUpload={this.onFileUpload}
                handleCancelImport={this.onCancelImport}
                handleShowLinkImport={this.showLinkImport}
              />
            )}

            {showImportByLink && (
              <ImportLinkUpload
                importByLinkError={error}
                handleChangeUrl={this.onChangeUrl}
                handleLinkUpload={this.onLinkUpload}
                handleCancelImport={this.onCancelImport}
              />
            )}
          </div>
        </div>
      </div>
    );
  }
}

ApiImportPage.defaultProps = {
  match: null,
  addApiItem: _.noop()
};

ApiImportPage.propTypes = {
  match: PropTypes.object,
  addApiItem: PropTypes.func
};

import React from 'react';
import PropTypes from 'prop-types';
import cx from 'classnames';
import { Collapse } from 'reactstrap';
import _ from 'lodash';

import localization from '../../utils/localization';
import './form-template.scss';

export const FormTemplate = props => {
  const {
    title,
    backgroundBlue,
    values,
    syncErrors,
    required,
    collapse,
    onToggle
  } = props;

  const collapseClass = cx('fdk-reg_collapse', {
    'fdk-reg_backgroundDefault': !backgroundBlue,
    'fdk-reg_backgroundBlue': backgroundBlue,
    'fdk-reg_collapse_open': collapse
  });

  const buttonClass = cx('fdk-collapseButton', 'fdk-btn-no-border', 'w-100', {
    'p-0': !backgroundBlue
  });

  const collapseIconClass = cx('fa', 'fa-2x', 'mr-2', {
    'fa-angle-down': !collapse,
    'fa-angle-up': collapse
  });

  const collapseContentClass = cx('mt-3', {
    'fdk-collapseContent': backgroundBlue
  });

  return (
    <div className={collapseClass}>
      <button className={buttonClass} onClick={onToggle}>
        <div className="d-flex align-items-center">
          <i className={collapseIconClass} />
          <h2 className="mb-0 text-ellipsis">{title}</h2>
          {required && (
            <span className="fdk-badge badge badge-secondary ml-2 fdk-text-size-small">
              {localization.helptext.required}
            </span>
          )}
          {syncErrors && (
            <div className="d-flex justify-content-end fdk-syncError-icon">
              <i className="fa fa-exclamation-triangle fdk-color-red ml-2" />
            </div>
          )}
        </div>
        {!collapse &&
          values && (
            <div className="d-flex text-left fdk-text-size-small fdk-color3">
              <i className="fa fa-2x fa-angle-down mr-2 visibilityHidden" />
              <span className="text-ellipsis">{values}</span>
            </div>
          )}
      </button>
      <Collapse className={collapseContentClass} isOpen={collapse}>
        {props.children}
      </Collapse>
    </div>
  );
};

FormTemplate.defaultProps = {
  values: null,
  title: null,
  backgroundBlue: false,
  syncErrors: false,
  required: false,
  children: null,
  collapse: false,
  onToggle: _.noop()
};

FormTemplate.propTypes = {
  values: PropTypes.string,
  title: PropTypes.string,
  backgroundBlue: PropTypes.bool,
  syncErrors: PropTypes.oneOfType([
    PropTypes.bool,
    PropTypes.object,
    PropTypes.array
  ]),
  required: PropTypes.bool,
  children: PropTypes.object,
  collapse: PropTypes.bool,
  onToggle: PropTypes.func
};

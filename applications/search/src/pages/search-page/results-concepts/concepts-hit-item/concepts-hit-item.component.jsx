import React from 'react';
import PropTypes from 'prop-types';
import cx from 'classnames';
import _ from 'lodash';
import { Link } from 'react-router-dom';

import localization from '../../../../lib/localization';
import { getTranslateText } from '../../../../lib/translateText';
import { PublisherLabel } from '../../../../components/publisher-label/publisher-label.component';
import { LinkExternal } from '../../../../components/link-external/link-external.component';
import './concepts-hit-item.scss';

const renderAddRemoveCompareButton = (
  item,
  showCompare,
  onAddConcept,
  onDeleteConcept,
  conceptIndex
) => {
  if (showCompare) {
    return (
      <button
        className="btn btn-primary fdk-button float-lg-right ml-0 mt-3 fade-in-400"
        onClick={() => {
          onAddConcept(item);
        }}
        type="button"
      >
        <i className="fa fa-plus mr-2" />
        {localization.compare.addCompare}
      </button>
    );
  }
  return (
    <button
      className="btn btn-primary fdk-button fdk-bg-color-white fdk-color-blue-dark float-lg-right ml-0 mt-3"
      onClick={() => {
        onDeleteConcept(conceptIndex);
      }}
      type="button"
    >
      <i className="fa fa-minus mr-2" />
      {localization.compare.removeCompare}
    </button>
  );
};

const renderTitle = (title, id, deprecated = false) => {
  if (!title) {
    return null;
  }

  const link = `/concepts/${id}`;

  return (
    <div className="mb-2 d-flex flex-wrap align-items-baseline">
      <Link
        className="search-hit__title-link"
        title={`${localization.apiLabel}: ${title}`}
        to={link}
      >
        <h2 className="mr-3" name={title}>
          {title}
        </h2>
      </Link>
      {deprecated && (
        <span className="fdk-color-dark-2 fdk-text-extra-strong fdk-text-size-medium">
          ({localization.deprecated})
        </span>
      )}
    </div>
  );
};

const renderPublisher = publisher => {
  if (!publisher) {
    return null;
  }

  return (
    <div className="mb-4">
      <PublisherLabel label="Ansvarlig:" tag="span" publisherItem={publisher} />
    </div>
  );
};

const renderDescription = description => {
  if (!description) {
    return null;
  }
  let descriptionText = getTranslateText(description);
  if (descriptionText && descriptionText.length > 220) {
    descriptionText = `${descriptionText.substr(0, 220)}...`;
  }
  return (
    <p>
      <span className="uu-invisible" aria-hidden="false">
        {localization.compare.description}
      </span>
      {descriptionText}
    </p>
  );
};

const renderIdentifiers = identifiers => {
  const children = items =>
    items.map(item => (
      <LinkExternal
        uri={_.get(item, 'uri')}
        prefLabel={_.get(item, 'prefLabel') || _.get(item, 'uri')}
      />
    ));

  if (!(identifiers && Array.isArray(identifiers))) {
    return null;
  }

  return (
    <div>
      <span>{localization.compare.source}:&nbsp;</span>
      {children(identifiers)}
    </div>
  );
};

export const ConceptsHitItem = props => {
  const {
    concepts,
    onAddConcept,
    onDeleteConcept,
    conceptIndex,
    fadeInCounter,
    result
  } = props;

  let showCompareButton = true;
  if (concepts) {
    showCompareButton = !_.some(
      concepts,
      term => term.uri === _.get(result, 'uri')
    );
  }

  const searchHitClass = cx('search-hit', {
    'fade-in-200': fadeInCounter === 0,
    'fade-in-300': fadeInCounter === 1,
    'fade-in-400': fadeInCounter === 2
  });

  return (
    <React.Fragment>
      <article
        className={searchHitClass}
        title={`Begrep: ${getTranslateText(_.get(result, 'prefLabel'))}`}
      >
        <span className="uu-invisible" aria-hidden="false">
          Søketreff begrep.
        </span>

        {renderAddRemoveCompareButton(
          result,
          showCompareButton,
          onAddConcept,
          onDeleteConcept,
          conceptIndex
        )}

        {renderTitle(
          getTranslateText(_.get(result, 'prefLabel')),
          _.get(result, 'id')
        )}

        {renderPublisher(_.get(result, 'publisher'))}

        {renderDescription(_.get(result, ['definition', 'text']))}

        {renderIdentifiers(_.get(result, 'harvest'))}
      </article>
    </React.Fragment>
  );
};

ConceptsHitItem.defaultProps = {
  result: null,
  concepts: null,
  fadeInCounter: 0
};

ConceptsHitItem.propTypes = {
  result: PropTypes.shape({}),
  concepts: PropTypes.array,
  onAddConcept: PropTypes.func.isRequired,
  onDeleteConcept: PropTypes.func.isRequired,
  conceptIndex: PropTypes.number.isRequired,
  fadeInCounter: PropTypes.number
};

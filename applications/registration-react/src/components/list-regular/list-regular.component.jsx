import React from 'react';
import PropTypes from 'prop-types';
import './list-regular.scss';

export const ListRegular = props => {
  const { title, bottomMargin } = props;
  return (
    <section
      className={`list-regular ${bottomMargin ? 'mb-5' : ''}`}
      name={title}
    >
      {title && (
        <div className="list-regular--item">
          <h3>{title}</h3>
        </div>
      )}
      {props.children}
    </section>
  );
};

ListRegular.defaultProps = {
  title: null,
  children: null,
  bottomMargin: true
};

ListRegular.propTypes = {
  title: PropTypes.string,
  children: PropTypes.oneOfType([PropTypes.array, PropTypes.object]),
  bottomMargin: PropTypes.bool
};

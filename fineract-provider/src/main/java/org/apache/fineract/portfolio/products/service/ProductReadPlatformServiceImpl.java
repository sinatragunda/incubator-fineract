/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 29 November 2022 at 00:48
 */
package org.apache.fineract.portfolio.products.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.products.data.ProductData;
import org.apache.fineract.portfolio.products.data.PropertyTypeData;
import org.apache.fineract.portfolio.products.enumerations.ACCOUNT_TYPE;
import org.apache.fineract.portfolio.products.enumerations.PRODUCT_TYPE;
import org.apache.fineract.portfolio.products.enumerations.PROPERTY_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductReadPlatformServiceImpl implements ProductReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final PropertyTypeLookupMapper propertyTypeLookupMapper = new PropertyTypeLookupMapper();
    private final ProductDataMapper productDataMapper = new ProductDataMapper();


    @Autowired
    public ProductReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);

    }

    private Consumer<ProductData> injectPropertyTypes = (e)->{
        Long id = e.getId();
        Collection<PropertyTypeData> propertyTypeDataCollection = retrieveAllPropertyTypesForLookupByProductId(id);
        e.setPropertyTypeDataCollection(propertyTypeDataCollection);
    };

    @Override
    public Collection<ProductData> retrieveAllByProductType(final PRODUCT_TYPE productType) {

        String sql = "select " + this.productDataMapper.schema() + " where p.product_type = ?";

        Collection<ProductData> productDataCollection = this.jdbcTemplate.query(sql, this.productDataMapper,
                new Object[] { productType.ordinal()});

        productDataCollection.stream().forEach(injectPropertyTypes);
        return productDataCollection;
    }

    @Override
    public ProductData retrieveOneByProductType(final Long productId ,final PRODUCT_TYPE productType) {

        final String sql = "select " + this.productDataMapper.schema() + " where p.product_id = ? and p.product_type = ?";
        final ProductData productData =  this.jdbcTemplate.queryForObject(sql, this.productDataMapper, new Object[] { productId ,productType.ordinal()});
        Stream.of(productData).forEach(injectPropertyTypes);
        return productData;

    }

    private static final class ProductDataMapper implements RowMapper<ProductData> {
        private final String schemaSql;
        public ProductDataMapper() {

            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("p.id as id, p.product_id as productId, p.is_active as active, ");
            sqlBuilder.append("p.product_type as productType, p.account_type as accountType, ");
            sqlBuilder.append("p.deduct_charges_on_balance as deductChargesOnAccountBalance ");
            sqlBuilder.append("from m_product p ");

            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public ProductData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Boolean isActive = rs.getBoolean("active");
            final Boolean deductChargesOnAccountBalance = rs.getBoolean("deductChargesOnAccountBalance");
            final Long productId = rs.getLong("productId");
            final Integer productTypeInt = rs.getInt("productType");
            final Integer accountTypeInt = rs.getInt("accountType");

            final PRODUCT_TYPE productType = PRODUCT_TYPE.fromInt(productTypeInt);
            final ACCOUNT_TYPE accountType  = ACCOUNT_TYPE.fromInt(accountTypeInt);

            ProductData productData = new ProductData(id ,productId ,productType ,accountType ,isActive ,deductChargesOnAccountBalance ,null);
            return productData;

        }
    }

    private static final class PropertyTypeLookupMapper implements RowMapper<PropertyTypeData> {

        public String schema() {
            return " pt.id as id, pt.property_type as propertyType from m_property_type pt";
        }

        @Override
        public PropertyTypeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Integer propertyTypeInt = rs.getInt("propertyType");

            final PROPERTY_TYPE propertyType = PROPERTY_TYPE.fromInt(propertyTypeInt);

            return PropertyTypeData.lookup(id, propertyType);
        }
    }

    /**
     * Added 29/11/2022 at 0109
     * Lookup by Product Id ,this being productid of productsettings not the underlying product itself
     */
    @Override
    public Collection<PropertyTypeData> retrieveAllPropertyTypesForLookupByProductId(Long productId) {

        String sql = String.format("select %s where pt.product_id = ?",this.propertyTypeLookupMapper.schema());
        return this.jdbcTemplate.query(sql, this.propertyTypeLookupMapper, new Object[] { productId });
    }
}

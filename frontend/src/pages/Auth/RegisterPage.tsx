import React, { useState } from 'react';
import {
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  Box,
  Grid,
  FormControl,
  FormLabel,
  RadioGroup,
  FormControlLabel,
  Radio,
  InputAdornment,
  IconButton,
  LinearProgress,
  Divider,
} from '@mui/material';
import {
  Person,
  Email,
  Lock,
  Visibility,
  VisibilityOff,
  Business,
  Phone,
} from '@mui/icons-material';
import { useForm, Controller } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { RegisterRequest } from '../../types';

const RegisterPage: React.FC = () => {
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const { control, handleSubmit, formState: { errors }, watch } = useForm<RegisterRequest>({
    mode: 'onChange',
    defaultValues: {
      role: 'USER',
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
      firstName: '',
      lastName: '',
      phoneNumber: '',
      businessName: '',
      businessType: '',
    },
  });

  const watchedRole = watch('role');

  const onSubmit = async (data: RegisterRequest) => {
    try {
      setIsLoading(true);
      
      // Simulate API call for now
      console.log('Registration data:', data);
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      toast.success('Registration successful! Please check your email to verify your account.');
      navigate('/login', { 
        state: { 
          message: 'Registration successful! Please verify your email before logging in.' 
        }
      });
    } catch (error: any) {
      console.error('Registration error:', error);
      toast.error('Registration failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Container component="main" maxWidth="md" sx={{ py: 8 }}>
      <Paper
        elevation={6}
        sx={{
          p: 4,
          background: 'linear-gradient(145deg, #ffffff 0%, #f5f5f5 100%)',
          borderRadius: 3,
        }}
      >
        <Box textAlign="center" mb={4}>
          <Typography
            component="h1"
            variant="h4"
            fontWeight="bold"
            color="primary"
            gutterBottom
          >
            Create Your Account
          </Typography>
          <Typography variant="body1" color="textSecondary">
            Join thousands of users and sellers on our platform
          </Typography>
        </Box>

        {isLoading && <LinearProgress sx={{ mb: 2 }} />}

        <form onSubmit={handleSubmit(onSubmit)}>
          <Grid container spacing={3}>
            {/* Basic Information */}
            <Grid item xs={12}>
              <Typography variant="h6" gutterBottom color="primary">
                Basic Information
              </Typography>
            </Grid>
            
            <Grid item xs={12} sm={6}>
              <Controller
                name="username"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    label="Username"
                    variant="outlined"
                    error={!!errors.username}
                    helperText={errors.username?.message}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <Person color="action" />
                        </InputAdornment>
                      ),
                    }}
                  />
                )}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <Controller
                name="email"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    label="Email Address"
                    type="email"
                    variant="outlined"
                    error={!!errors.email}
                    helperText={errors.email?.message}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <Email color="action" />
                        </InputAdornment>
                      ),
                    }}
                  />
                )}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <Controller
                name="firstName"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    label="First Name"
                    variant="outlined"
                    error={!!errors.firstName}
                    helperText={errors.firstName?.message}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <Person color="action" />
                        </InputAdornment>
                      ),
                    }}
                  />
                )}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <Controller
                name="lastName"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    label="Last Name"
                    variant="outlined"
                    error={!!errors.lastName}
                    helperText={errors.lastName?.message}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <Person color="action" />
                        </InputAdornment>
                      ),
                    }}
                  />
                )}
              />
            </Grid>

            <Grid item xs={12}>
              <Controller
                name="phoneNumber"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    label="Phone Number"
                    variant="outlined"
                    error={!!errors.phoneNumber}
                    helperText={errors.phoneNumber?.message}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <Phone color="action" />
                        </InputAdornment>
                      ),
                    }}
                  />
                )}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <Controller
                name="password"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    label="Password"
                    type={showPassword ? 'text' : 'password'}
                    variant="outlined"
                    error={!!errors.password}
                    helperText={errors.password?.message}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <Lock color="action" />
                        </InputAdornment>
                      ),
                      endAdornment: (
                        <InputAdornment position="end">
                          <IconButton
                            aria-label="toggle password visibility"
                            onClick={() => setShowPassword(!showPassword)}
                            edge="end"
                          >
                            {showPassword ? <Visibility /> : <Visibility />}
                          </IconButton>
                        </InputAdornment>
                      ),
                    }}
                  />
                )}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <Controller
                name="confirmPassword"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    label="Confirm Password"
                    type={showConfirmPassword ? 'text' : 'password'}
                    variant="outlined"
                    error={!!errors.confirmPassword}
                    helperText={errors.confirmPassword?.message}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <Lock color="action" />
                        </InputAdornment>
                      ),
                      endAdornment: (
                        <InputAdornment position="end">
                          <IconButton
                            aria-label="toggle confirm password visibility"
                            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                            edge="end"
                          >
                            {showConfirmPassword ? <Visibility /> : <Visibility />}
                          </IconButton>
                        </InputAdornment>
                      ),
                    }}
                  />
                )}
              />
            </Grid>

            {/* Role Selection */}
            <Grid item xs={12}>
              <Divider sx={{ my: 2 }} />
              <Typography variant="h6" gutterBottom color="primary">
                Account Type
              </Typography>
            </Grid>

            <Grid item xs={12}>
              <FormControl component="fieldset" error={!!errors.role}>
                <FormLabel component="legend" sx={{ mb: 2, fontWeight: 'bold' }}>
                  Select Your Role
                </FormLabel>
                <Controller
                  name="role"
                  control={control}
                  render={({ field }) => (
                    <RadioGroup {...field} row>
                      <FormControlLabel 
                        value="USER" 
                        control={<Radio />} 
                        label={
                          <Box>
                            <Typography variant="subtitle1" fontWeight="bold">Customer</Typography>
                            <Typography variant="body2" color="textSecondary">
                              Browse and purchase products from various sellers
                            </Typography>
                          </Box>
                        }
                        sx={{ 
                          border: '1px solid',
                          borderColor: field.value === 'USER' ? 'primary.main' : 'grey.300',
                          borderRadius: 2,
                          p: 2,
                          m: 1,
                          flex: 1,
                        }}
                      />
                      <FormControlLabel 
                        value="SELLER" 
                        control={<Radio />} 
                        label={
                          <Box>
                            <Typography variant="subtitle1" fontWeight="bold">Seller</Typography>
                            <Typography variant="body2" color="textSecondary">
                              Sell your products and manage your business
                            </Typography>
                          </Box>
                        }
                        sx={{ 
                          border: '1px solid',
                          borderColor: field.value === 'SELLER' ? 'primary.main' : 'grey.300',
                          borderRadius: 2,
                          p: 2,
                          m: 1,
                          flex: 1,
                        }}
                      />
                    </RadioGroup>
                  )}
                />
                {errors.role && (
                  <Typography color="error" variant="caption" sx={{ mt: 1 }}>
                    {errors.role.message}
                  </Typography>
                )}
              </FormControl>
            </Grid>

            {/* Business Information (for sellers) */}
            {watchedRole === 'SELLER' && (
              <>
                <Grid item xs={12}>
                  <Divider sx={{ my: 2 }} />
                  <Typography variant="h6" gutterBottom color="primary">
                    Business Information
                  </Typography>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Controller
                    name="businessName"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        label="Business Name"
                        variant="outlined"
                        error={!!errors.businessName}
                        helperText={errors.businessName?.message}
                        InputProps={{
                          startAdornment: (
                            <InputAdornment position="start">
                              <Business color="action" />
                            </InputAdornment>
                          ),
                        }}
                      />
                    )}
                  />
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Controller
                    name="businessType"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        label="Business Type"
                        variant="outlined"
                        error={!!errors.businessType}
                        helperText={errors.businessType?.message}
                        InputProps={{
                          startAdornment: (
                            <InputAdornment position="start">
                              <Business color="action" />
                            </InputAdornment>
                          ),
                        }}
                      />
                    )}
                  />
                </Grid>
              </>
            )}

            {/* Submit Button */}
            <Grid item xs={12}>
              <Box sx={{ mt: 3 }}>
                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  size="large"
                  disabled={isLoading}
                  sx={{ py: 1.5 }}
                >
                  {isLoading ? 'Creating Account...' : 'Create Account'}
                </Button>
              </Box>
            </Grid>
          </Grid>
        </form>

        <Divider sx={{ my: 3 }} />

        <Box textAlign="center">
          <Typography variant="body2" color="textSecondary">
            Already have an account?{' '}
            <Link 
              to="/login" 
              style={{ 
                color: 'inherit', 
                textDecoration: 'none',
                fontWeight: 'bold',
              }}
            >
              Sign in here
            </Link>
          </Typography>
        </Box>
      </Paper>
    </Container>
  );
};

export default RegisterPage; 